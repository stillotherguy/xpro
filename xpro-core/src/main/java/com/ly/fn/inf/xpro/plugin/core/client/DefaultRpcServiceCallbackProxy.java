package com.ly.fn.inf.xpro.plugin.core.client;

import com.ly.fn.inf.rpc.annotaion.RpcService;
import com.ly.fn.inf.rpc.annotaion.Self;
import com.ly.fn.inf.xpro.plugin.core.InternalRpcServiceCallbackProxy;
import com.ly.fn.inf.xpro.plugin.core.api.ClientObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.api.ServiceClass;
import com.ly.fn.inf.xpro.plugin.core.api.ServiceDescription;
import com.ly.fn.inf.xpro.plugin.core.util.ReflectUtil;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rpc服务回调代理类。
 * 
 * 
 */
public class DefaultRpcServiceCallbackProxy implements InternalRpcServiceCallbackProxy {
    public class CallbackArgInvHandler implements InvocationHandler {
        private ServiceDescription serviceDescription;

        private Class<?> callbackInf;

        private Object targetObject;

        public CallbackArgInvHandler(ServiceDescription serviceDescription) {
            this.serviceDescription = serviceDescription;
        }

        public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getServiceDescription")) {
                return serviceDescription;
            } else {
                if (targetObject != null) {
                    return method.invoke(targetObject, args);
                } else {
                    Object cliObj = clientObjectFactory.getClientObject(callbackInf, serviceDescription.getServiceId(), serviceDescription.getServiceGroup());
                    return method.invoke(cliObj, args);
                }
            }
        }
    }

    private ClientObjectFactory clientObjectFactory;

    private Map<String, Object> cachedProxies = new ConcurrentHashMap<String, Object>();

    @Setter
    private String localGatewayName;

    @Setter
    private String localAppName;

    private <T> ServiceDescription getServiceDescription(Class<T> callbackInf, Class<?> rpcServiceClass, Object targetObj) {
        ServiceDescription serviceDescription = ServiceClass.getServiceDescription(rpcServiceClass, targetObj);
        RpcService rpcSrvAnno = rpcServiceClass.getAnnotation(RpcService.class);

        Class<?> infClazz = rpcServiceClass;
        if (rpcSrvAnno.serviceInterface().equals(Self.class) == false) {
            infClazz = rpcSrvAnno.serviceInterface();
        }

        if (ReflectUtil.isAncestor(callbackInf, infClazz) == false) {
            throw new RuntimeException("The RpcServiceClass=[" + infClazz.getName() + "] hasn't implemented the callback=[" + callbackInf.getName() + "].");
        }

        return serviceDescription;
    }

    @SuppressWarnings("unchecked")
    protected <T> T newCallback(Class<T> callbackInf, Class<?> rpcServiceClass, Object targetObj) {
        StringBuffer key = new StringBuffer();
        key.append(callbackInf.getName() + ":" + rpcServiceClass.getName());
        Object obj = cachedProxies.get(key.toString());
        if (obj != null) {
            // return cached proxy
            return (T) obj;
        }

        ServiceDescription serviceDescription = getServiceDescription(callbackInf, rpcServiceClass, targetObj);

        ClassLoader cl = callbackInf.getClassLoader();
        CallbackArgInvHandler handler = new CallbackArgInvHandler(serviceDescription);
        handler.callbackInf = callbackInf;
        handler.targetObject = targetObj;

        T proxy = (T) Proxy.newProxyInstance(cl, new Class[] {callbackInf, RpcServiceCallback.class}, handler);
        cachedProxies.put(key.toString(), proxy);
        return proxy;
    }

    public <T> T newCallback(Class<T> callbackInf, Class<?> rpcServiceClass) {
        return newCallback(callbackInf, rpcServiceClass, null);
    }

    public <T> T newCallback(Class<T> callbackInf, Object targetObject) {
        return newCallback(callbackInf, targetObject.getClass(), targetObject);
    }

    public ClientObjectFactory getClientObjectFactory() {
        return clientObjectFactory;
    }

    public void setClientObjectFactory(ClientObjectFactory clientObjectFactory) {
        this.clientObjectFactory = clientObjectFactory;
    }

}
