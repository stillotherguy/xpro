package com.ly.fn.inf.xpro.plugin.api;

/**
 * Rpc服务回调代理类。
 * 
 * 
 */
public interface RpcServiceCallbackProxy {
    <T> T newCallback(Class<T> callbackInf, Class<?> rpcServiceClass);

    <T> T newCallback(Class<T> callbackInf, Object targetObject);
}
