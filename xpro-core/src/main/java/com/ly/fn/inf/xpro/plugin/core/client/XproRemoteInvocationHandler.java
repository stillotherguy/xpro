package com.ly.fn.inf.xpro.plugin.core.client;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import com.ly.fn.inf.util.HostInfo;
import com.ly.fn.inf.util.JSON;
import com.ly.fn.inf.util.ZipUtil;
import com.ly.fn.inf.xpro.plugin.api.AsyncCallResult;
import com.ly.fn.inf.xpro.plugin.api.PropertyNames;
import com.ly.fn.inf.xpro.plugin.api.RpcClientContextAccessor;
import com.ly.fn.inf.xpro.plugin.core.*;
import com.ly.fn.inf.xpro.plugin.core.api.*;
import com.ly.fn.inf.xpro.plugin.core.cache.RequestScopeCacheService;
import com.ly.fn.inf.xpro.plugin.core.protocol.*;
import com.ly.fn.inf.xpro.plugin.core.server.InternalServerObjectRegistry;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mitsui
 * @since 2017年11月27日
 */
@Slf4j
public class XproRemoteInvocationHandler implements InvocationHandler {

    private static ListeningExecutorService asyncExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    @Setter
    private RemoteStub remoteStub;

    @Setter
    private RestTemplate restTemplate;

    @Setter
    private RestTemplate ldRestTemplate;

    @Setter
    private ServiceClass serviceClass;

    @Setter
    private ProtocolFactorySelector protocolFactorySelector;

    @Setter
    private InternalServerObjectRegistry serverObjectRegistry;

    @Setter
    private ExecutorService asyncCallResultHandlerExecutorService;

    @Setter
    private XproProperties xproProperties;

    @Setter
    private DiscoveryClient discoveryClient;

    @Setter
    private String gatewayName;

    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        remoteStub.build();
        final Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(RemoteObject.class)) {
            return method.invoke(this.remoteStub, args);
        }
        if (ReflectionUtils.isToStringMethod(method) || ReflectionUtils.isEqualsMethod(method) || ReflectionUtils.isHashCodeMethod(method) || ReflectionUtils.isObjectMethod(method)) {
            return method.invoke(this.remoteStub, args);
        }

        ServiceMethod srvMethod = serviceClass.getMethod(method);
        final ServiceDescription srvDesc = serviceClass.getDescription();
        final ServiceMethodDescription methodDesc = srvMethod.getDescription();

        if (log.isDebugEnabled()) {
            log.debug("calling the rpcService=[" + srvDesc.getServiceId() + "], method=[" + method.getName() + "].");
        }

        if (methodDesc.isRequestScopeCache()) {
            // 请求范围缓存
            try {
                Object ret = RequestScopeCacheService.getCachedReturn(srvDesc.getServiceId(), method.getName(), args);
                if (log.isDebugEnabled()) {
                    log.debug("found the cached object.");
                }

                return ret;
            } catch (Throwable e) {
                throw new RuntimeException("NotFoundCachedException");
            }
        }

        ServiceMethod serviceMethod = serviceClass.getMethod(method);

        RpcRequestPreparer preparer = new RpcRequestPreparer(serviceClass, serverObjectRegistry, true);
        preparer.prepare(method, args);

        final String contentType = xproProperties.getContentType();
        ProtocolFactory protocolFactory = protocolFactorySelector.select(contentType);
        RequestOutProtocol requestOutProtocol = protocolFactory.newRequestOutProtocol();
        requestOutProtocol.writeHeader(preparer.getHeader());
        requestOutProtocol.writeArgs(method, preparer.getArgs());

        // 默认选择负载均衡REST客户端
        RestTemplate target = ldRestTemplate;
        String host = serviceClass.getDescription().getAppName();
        boolean isCallback = srvDesc.isCallbackObject() && srvDesc.getCallbackAddress() != null;
        if (isCallback) {
            host = srvDesc.getCallbackAddress();
            // 原路回调选择普通REST客户端
            target = restTemplate;
        }

        UrlConstructor urlConstructor = new GatewayAwareUrlConstructor(serviceClass, serviceMethod, discoveryClient);
        final String url = urlConstructor.construct(gatewayName, host, isCallback);

        final Message reqMsg = requestOutProtocol.getMessage();

        System.out.println(JSON.getDefault().toJSONString(reqMsg));

        byte[] data = reqMsg.getData();
        if (data.length > 1024 * 128) {
            // 大于128K的数据，启动压缩
            data = ZipUtil.zipBytes(data);
            reqMsg.setProperty(PropertyNames.ZIP_FLAG, "1");
            reqMsg.setData(data);
        }

        long ts = System.currentTimeMillis();
        reqMsg.setCreateTime(ts);
        reqMsg.setProperty(PropertyNames.CLIENT_TIME, Long.toString(ts, 10));
        reqMsg.setProperty(PropertyNames.CLIENT_SRV_INFO, HostInfo.getHostInfo());

        MultiValueMap headers = new LinkedMultiValueMap();
        headers.put(HttpHeaders.CONTENT_TYPE, Lists.newArrayList(MediaType.APPLICATION_JSON_UTF8_VALUE));

        final RequestEntity requestEntity = new RequestEntity(reqMsg, headers, HttpMethod.POST, new URI(url));

        if (methodDesc.isAsynchronous()) {
            return asyncCall(method, args, serviceMethod, protocolFactory, srvDesc, url, requestEntity, target);
        }

        try {
            return syncCall(method, args, serviceMethod, protocolFactory, requestEntity, target);
        } finally {
            preparer.afterCall();
        }
    }

    private Object syncCall(Method method, Object[] args, ServiceMethod serviceMethod, ProtocolFactory protocolFactory, RequestEntity requestEntity, RestTemplate target) throws Throwable {
        ResponseEntity<Message> responseEntity = target.exchange(requestEntity, Message.class);

        Message responseMsg = responseEntity.getBody();
        ReplyInProtocol replyInProtocol = protocolFactory.newReplyInProtocol(responseMsg);

        CallResult callResult = processReply(replyInProtocol, method, args, serviceMethod.getDescription());

        if (callResult.exception == false) {
            return callResult.returnObject;
        } else {
            StackTraceElement[] remoteStackTrace = callResult.e.getStackTrace();
            if (remoteStackTrace != null) {
                StackTraceElement[] localStackTrace = Thread.currentThread().getStackTrace();
                StackTraceElement sepEle = new StackTraceElement("$XproProxy[" + RpcClientContextAccessor.getLastCallServerInfo() + "]", "invoke", null, 0);
                StackTraceElement[] fullStackTrace = new StackTraceElement[localStackTrace.length + remoteStackTrace.length];
                System.arraycopy(remoteStackTrace, 0, fullStackTrace, 0, remoteStackTrace.length);
                fullStackTrace[remoteStackTrace.length] = sepEle;
                System.arraycopy(localStackTrace, 1, fullStackTrace, remoteStackTrace.length + 1, localStackTrace.length - 1);

                callResult.e.setStackTrace(fullStackTrace);
            }

            throw callResult.e;
        }
    }

    private Object asyncCall(final Method method, final Object[] args, final ServiceMethod serviceMethod, final ProtocolFactory protocolFactory, final ServiceDescription srvDesc, final String url, final RequestEntity requestEntity, final RestTemplate target) {
        AsyncCallResultHolder resultHolder = ExpectAsyncCallAccessor.getAsyncCallResult();
        AsyncCallResult asyncCallResult2 = null;
        // TODO 超时
        final long timeout;
        if (resultHolder != null) {
            timeout = resultHolder.getTimeout();
            asyncCallResult2 = resultHolder.getAsyncCallResult();
        } else {
            // 缺省60秒等待异步调用结果
            timeout = 60000;
        }

        ListenableFuture<ResponseEntity<Message>> retFuture = asyncExecutorService.submit(new Callable<ResponseEntity<Message>>() {
            @Override
            public ResponseEntity<Message> call() throws Exception {
                return target.exchange(requestEntity, Message.class);
            }
        });

        final AsyncCallResult asyncCallResult = asyncCallResult2;

        Futures.addCallback(retFuture, new FutureCallback<ResponseEntity<Message>>() {
            @Override
            public void onSuccess(final ResponseEntity<Message> result) {
                if (asyncCallResult == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Success to async call.");
                    }

                    return;
                }

                Message responseMsg = result.getBody();
                ReplyInProtocol replyInProtocol = protocolFactory.newReplyInProtocol(responseMsg);
                CallResult callResult = processReply(replyInProtocol, method, args, serviceMethod.getDescription());
                if (callResult.exception) {
                    asyncCallResult.onException(args, callResult.e);
                } else {
                    asyncCallResult.onSuccess(args, callResult.returnObject);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (asyncCallResult == null) {
                    log.error("Fail to async call serviceId=[" + srvDesc.getServiceId() + "].[" + method.getName() + "], serverInfo=[" + url + "].", t);
                    return;
                }

                asyncCallResult.onException(args, t);
            }
        }, asyncCallResultHandlerExecutorService);

        return null;
    }

    private class CallResult {
        private boolean exception;
        private Object returnObject;
        private Throwable e;
    }

    private CallResult processReply(ReplyInProtocol inProtocol, Method method, Object[] args, ServiceMethodDescription description) {
        CallResult callResult = new CallResult();
        ReplyHeader replyHeader = inProtocol.readHeader();

        ProtectedRpcClientContextAccessor.setLastCallServerInfo(replyHeader.getServerInfo());
        if (replyHeader.getType().equals(ReplyHeader.TYPE_RETURN)) {
            Object retObj = inProtocol.readRetObject(method);
            if (description.isRequestScopeCache()) {
                // 请求范围缓存
                RequestScopeCacheService.cacheReturn(serviceClass.getDescription().getServiceId(), method.getName(), args, retObj);
            }
            callResult.returnObject = retObj;
        } else if (replyHeader.getType().equals(ReplyHeader.TYPE_EXCEPTION)) {
            Throwable e = inProtocol.readException();
            if (e instanceof NotFoundServiceException || e instanceof NoSuchMethodException || e instanceof AmbiguousMethodException) {
                // 配置出现严重问题，找不到服务
                log.error("Service process request meet error, serverInfo=[" + replyHeader.getServerInfo() + "].", e);
            }

            callResult.exception = true;
            callResult.e = e;
        }

        return callResult;
    }
}
