package com.ly.fn.inf.xpro.plugin.core;

import com.ly.fn.inf.rpc.annotaion.Lifecycle;
import com.ly.fn.inf.xpro.plugin.core.api.*;
import com.ly.fn.inf.xpro.plugin.core.client.RpcServiceCallback;
import com.ly.fn.inf.xpro.plugin.core.protocol.CallbackArg;
import com.ly.fn.inf.xpro.plugin.core.protocol.RequestHeader;
import com.ly.fn.inf.xpro.plugin.core.protocol.RpcHeaderPropNames;
import com.ly.fn.inf.xpro.plugin.core.server.AppInfo;
import com.ly.fn.inf.xpro.plugin.core.server.InternalServerObjectRegistry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Rpc请求准备类，非Singleton类。
 */
@Slf4j
public class RpcRequestPreparer {

    /**
     * 服务类
     */
    private ServiceClass serviceClass;

    /**
     * 请求头对象
     */
    private RequestHeader header;

    /**
     * 和同步调用同生命周期的回调服务编号组
     */
    private Set<String> withSyncCallCalbackServiceIds = new HashSet<String>();

    /**
     * 请求参数列表
     */
    private Object[] args;

    /**
     * 服务端对象注册器（用于CallbackObject注册）
     */
    private InternalServerObjectRegistry serverObjectRegistry;

    /**
     * 跟踪代码标志
     */
    private boolean trackingCode = true;

    public RpcRequestPreparer(ServiceClass srvClass, InternalServerObjectRegistry serverObjectRegistry, boolean trackingCode) {
        this.serviceClass = srvClass;
        this.serverObjectRegistry = serverObjectRegistry;
        this.trackingCode = trackingCode;
    }

    public void prepare(Method method, Object[] oArgs) {
        ServiceMethod srvMethod = serviceClass.getMethod(method);
        ServiceDescription srvDesc = serviceClass.getDescription();

        header = new RequestHeader();

        header.setServiceId(srvDesc.getServiceId());
        header.setServiceGroup(srvDesc.getServiceGroup());
        header.setMethod(method.getName());
        header.setArgsSignature(srvMethod.getArgsSignature());
        header.setArgsCount(oArgs == null ? 0 : oArgs.length);
        header.setTimeout(srvMethod.getDescription().getTimeout());
        header.setGatewayName(serviceClass.getDescription().getGatewayName());
        header.setApplicationName(serviceClass.getDescription().getAppName());

        String trackingCode = RpcCallTracker.getTrackingCode();
        if (trackingCode == null) {
            if (this.trackingCode) {
                trackingCode = RpcCallTracker.newTrackingCode();
            }
        }

        if (trackingCode != null) {
            header.setProp(RpcHeaderPropNames.TRACKING_CODE, trackingCode);
        }

        if (oArgs != null) {
            args = new Object[oArgs.length];
            for (int i = 0; i < oArgs.length; i++) {
                Object arg = oArgs[i];
                if (arg == null) {
                    args[i] = null;
                    continue;
                }

                ServiceMethodParameterDescription paraDesc = srvMethod.getDescription().getParameterDescription(i);
                if (paraDesc.getCallback() != null) {
                    if (paraDesc.getCallback().getLifecycle() == Lifecycle.SERVICE) {
                        if (!(arg instanceof RpcServiceCallback)) {
                            // 必须是Rpc服务回调
                            throw new RuntimeException("Only support callback to a rpcService.");
                        }
                    }

                    if (arg instanceof RpcServiceCallback) {
                        // callback is a RpcService
                        RpcServiceCallback rpcSrvCb = (RpcServiceCallback) arg;
                        ServiceDescription callbackSrvDesc = rpcSrvCb.getServiceDescription();
                        CallbackArg callbackArg = new CallbackArg();
                        callbackArg.setCallbackType(CallbackArg.CALLBACK_TYPE_LNK_SERVICE);
                        callbackArg.setServiceId(callbackSrvDesc.getServiceId());
                        callbackArg.setServiceGroup(callbackSrvDesc.getServiceGroup());
                        callbackArg.setGatewayName(callbackSrvDesc.getGatewayName());
                        callbackArg.setAppName(callbackSrvDesc.getAppName());

                        args[i] = callbackArg;
                    } else {
                        // callback is normal java object
                        Class<?> paraClass = method.getParameterTypes()[i];
                        String srvId = serverObjectRegistry.registerCallbackObject(arg, paraClass, paraDesc.getCallback());
                        CallbackArg callbackArg = new CallbackArg();
                        callbackArg.setCallbackType(CallbackArg.CALLBACK_TYPE_ANONYMOUS);
                        callbackArg.setGatewayName(srvDesc.getGatewayName());
                        callbackArg.setAppName(srvDesc.getAppName());
                        callbackArg.setServiceId(srvId);
                        callbackArg.setServiceGroup(srvDesc.getServiceGroup());
                        callbackArg.setServerAddress(AppInfo.getInstance().toAddress());

                        if (paraDesc.getCallback().getLifecycle() == Lifecycle.WITH_SYNC_CALL) {
                            withSyncCallCalbackServiceIds.add(srvId);
                        }

                        args[i] = callbackArg;
                    }
                } else {
                    args[i] = oArgs[i];
                }
            }
        }
    }

    public void afterCall() {
        for (String callbackServiceId : withSyncCallCalbackServiceIds) {
            serverObjectRegistry.unregisterCallbackObject(callbackServiceId);
        }
    }

    public ServiceClass getServiceClass() {
        return serviceClass;
    }

    public RequestHeader getHeader() {
        return header;
    }

    public Object[] getArgs() {
        return args;
    }

    public ServerObjectRegistry getServerObjectRegistry() {
        return serverObjectRegistry;
    }
}
