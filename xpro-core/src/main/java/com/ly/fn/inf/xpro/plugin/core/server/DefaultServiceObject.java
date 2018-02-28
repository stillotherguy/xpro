package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.rpc.annotaion.Lifecycle;
import com.ly.fn.inf.rpc.annotaion.Sla;
import com.ly.fn.inf.xpro.plugin.core.api.*;
import com.ly.fn.inf.xpro.plugin.core.cache.RequestScopeCacheService;
import com.ly.fn.inf.xpro.plugin.core.client.InternalClientObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 缺省服务对象实现类。
 */
public class DefaultServiceObject implements ServiceObject {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 目标对象
     */
    private Object targetObject;

    /**
     * 服务类
     */
    private ServiceClass serviceClass;

    /**
     * 目标对象类
     */
    private Class<?> targetClass;

    /**
     * 客户端对象工厂对象（用于Callback对象生成）
     */
    private InternalClientObjectFactory clientObjectFactory;

    /**
     * 服务对象注册器
     */
    private InternalServerObjectRegistry serverObjectRegistry;

    private DefaultServiceObject() {}

    private static void overlapObjectSla(DefaultServiceObject srvObj, Class<?> targetClass) {
        // overlap method sla
        for (ServiceMethod srvMethod : srvObj.serviceClass.getMethods()) {
            Method method;
            try {
                method = targetClass.getMethod(srvMethod.getMethod().getName(), srvMethod.getMethod().getParameterTypes());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            Sla sla = method.getAnnotation(Sla.class);
            if (sla != null) {
                srvMethod.getDescription().setAvgTime(sla.avgTime());
                srvMethod.getDescription().setTimeout(sla.timeout());
                srvMethod.getDescription().setPriority(sla.priority());
            }
        }
    }

    public static DefaultServiceObject newServiceObject(Object targetObject, Class<?> targetClass, Class<?> serviceInterface, InternalClientObjectFactory clientObjectFactory,
            InternalServerObjectRegistry serverObjectRegistry) {
        DefaultServiceObject srvObj = new DefaultServiceObject();
        srvObj.targetObject = targetObject;
        if (targetClass == null) {
            srvObj.targetClass = targetObject.getClass();
        } else {
            srvObj.targetClass = targetClass;
        }
        srvObj.serviceClass = ServiceClass.newServiceClass(serviceInterface, targetObject);
        srvObj.clientObjectFactory = clientObjectFactory;
        srvObj.serverObjectRegistry = serverObjectRegistry;
        // overlap method sla
        overlapObjectSla(srvObj, targetClass);
        return srvObj;
    }

    public static DefaultServiceObject newCallbackObject(Object targetObject, Class<?> serviceInterface, CallbackDescription callbackDesc,
                                                         InternalClientObjectFactory clientObjectFactory, InternalServerObjectRegistry serverObjectRegistry) {
        DefaultServiceObject srvObj = new DefaultServiceObject();
        srvObj.targetObject = targetObject;
        srvObj.targetClass = targetObject.getClass();
        srvObj.serviceClass = ServiceClass.newCallbackClass(serviceInterface, callbackDesc);
        srvObj.clientObjectFactory = clientObjectFactory;
        srvObj.serverObjectRegistry = serverObjectRegistry;
        overlapObjectSla(srvObj, targetObject.getClass());
        return srvObj;
    }

    @Override
    public String getGatewayName() {
        return serviceClass.getDescription().getGatewayName();
    }

    @Override
    public String getAppName() {
        return serviceClass.getDescription().getAppName();
    }

    public String getServiceId() {
        return serviceClass.getDescription().getServiceId();
    }

    public String getServiceGroup() {
        return serviceClass.getDescription().getServiceGroup();
    }

    public boolean isCallbackObject() {
        return serviceClass.getDescription().isCallbackObject();
    }

    protected void doInvoke(Method method, RequestInProtocol in, ReplyOutProtocol out) throws Throwable {
        ServiceMethod srvMethod = serviceClass.getMethod(method);

        Object[] oArgs = in.readArgs(method);
        Object[] args = new Object[oArgs.length];
        for (int i = 0; i < args.length; i++) {
            ServiceMethodParameterDescription paraDesc = srvMethod.getDescription().getParameterDescription(i);
            if (paraDesc.getCallback() != null && oArgs[i] != null) {
                // Callback arg
                if (!(oArgs[i] instanceof CallbackArg)) {
                    throw new IllegalArgumentException("Args[" + i + "] must be the callback stub.");
                }

                Class<?> callbackInf = method.getParameterTypes()[i];
                CallbackArg cbArg = (CallbackArg) oArgs[i];
                if (cbArg.getCallbackType().equals(CallbackArg.CALLBACK_TYPE_LNK_SERVICE)) {
                    String gatewayName = cbArg.getGatewayName();
                    String appName = cbArg.getAppName();
                    String srvId = cbArg.getServiceId();
                    String srvGrp = cbArg.getServiceGroup();
                    String srvAddr = cbArg.getServerAddress();
                    String srvContentType = cbArg.getContentType();
                    args[i] = clientObjectFactory.getClientObjectForCallback(gatewayName, appName, srvId, srvGrp, srvAddr, srvContentType, callbackInf, false);
                } else {
                    // invoke object only has a random serviceId
                    String srvId = cbArg.getServiceId();
                    String serverAddress = cbArg.getServerAddress();
                    String appName = cbArg.getAppName();
                    args[i] = clientObjectFactory.getClientObjectForCallback(null, appName, srvId,"callback", serverAddress, in.getMessage().getContentType(), callbackInf, true);
                }
            } else {
                args[i] = oArgs[i];
            }
        }

        RequestHeader reqHeader = in.readHeader();
        ReplyHeader replyHeader = new ReplyHeader();

        // init request scope cache
        RequestScopeCacheService.initCache();
        Object retObj;
        if (in.readHeader().getCrossDMZFlag() == null || in.readHeader().getCrossDMZFlag() == false) {
            // 跨越安全区域，去除服务器信息
            replyHeader.setServerInfo(AppInfo.getInstance().toAddress());
        }

        try {
            retObj = method.invoke(targetObject, args);
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }

            throw e;
        } finally {
            // clear request scope cache
            RequestScopeCacheService.clearCache();
        }

        replyHeader.setType(ReplyHeader.TYPE_RETURN);
        out.writeHeader(replyHeader);
        out.writeRetObject(method, method.getGenericReturnType(), retObj);
    }

    public void invoke(Method method, RequestInProtocol in, ReplyOutProtocol out) {
        try {
            ServiceDescription srvDesc = serviceClass.getDescription();
            if (srvDesc.isCallbackObject()) {
                if (srvDesc.getCallbackLifecycle() == Lifecycle.ONE_TIME) {
                    // one-time callback
                    boolean flag = serverObjectRegistry.unregisterCallbackObject(srvDesc.getServiceId());
                    if (flag == false) {
                        throw new RuntimeException("CallbackObject already has been expired.");
                    }
                }
            }

            doInvoke(method, in, out);
        } catch (Throwable e) {
            if (e instanceof RuntimeException && !(e instanceof IllegalArgumentException)) {
                logger.error("ServiceObject invoke meet error.", e);
            }

            out.reset();
            ReplyExceptionHelper.replyException(in, out, e);
        }
    }

    public Class<?> getServiceInterface() {
        return serviceClass.getInfClazz();
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public ServiceClass getServiceClass() {
        return serviceClass;
    }

    public InternalClientObjectFactory getClientObjectFactory() {
        return clientObjectFactory;
    }

    public InternalServerObjectRegistry getServerObjectRegistry() {
        return serverObjectRegistry;
    }
}
