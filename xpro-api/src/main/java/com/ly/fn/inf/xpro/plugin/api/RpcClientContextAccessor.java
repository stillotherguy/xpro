package com.ly.fn.inf.xpro.plugin.api;

/**
 * Rpc客户端上下文访问类。
 * 
 * 
 */
public class RpcClientContextAccessor {
    protected static ThreadLocal<RpcClientContext> contextHolder = new ThreadLocal<RpcClientContext>();

    protected static volatile RpcServiceCallbackProxy proxy = null;

    /**
     * 获得最近一次调用的服务器信息
     * 
     * @return
     */
    public static String getLastCallServerInfo() {
        RpcClientContext ctx = contextHolder.get();
        if (ctx == null) {
            return null;
        }

        return ctx.getLastCallServerInfo();
    }

    /**
     * 获得对应RpcService的Callback对象
     * 
     * @param callbackInf
     * @param rpcServiceClass
     * @return
     */
    public static <T> T newCallbackForRpcService(Class<T> callbackInf, Class<?> rpcServiceClass) {
        if (proxy == null) {
            throw new RuntimeException("Need init inf-rpc module firstly.");
        }

        return proxy.newCallback(callbackInf, rpcServiceClass);
    }

    /**
     * 获得对应RpcService的Callback对象
     * 
     * @param callbackInf
     * @return
     */
    public static <T> T newCallbackForRpcService(Class<T> callbackInf, Object targetObject) {
        if (proxy == null) {
            throw new RuntimeException("Need init inf-rpc module firstly.");
        }

        return proxy.newCallback(callbackInf, targetObject);
    }
}
