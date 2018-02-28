package com.ly.fn.inf.xpro.plugin.core.client;


import com.ly.fn.inf.xpro.plugin.api.RpcClientContextAccessor;
import com.ly.fn.inf.xpro.plugin.api.RpcServiceCallbackProxy;

/**
 * 保护RpcClientContext访问类。
 * 
 * 
 */
public class ProtectedRpcClientContextAccessor extends RpcClientContextAccessor {
    public static void setLastCallServerInfo(String serverInfo) {
        DefaultRpcClientContext ctx = (DefaultRpcClientContext) RpcClientContextAccessor.contextHolder.get();
        if (ctx == null) {
            ctx = new DefaultRpcClientContext();
            RpcClientContextAccessor.contextHolder.set(ctx);
        }

        ctx.setLastCallServerInfo(serverInfo);
    }

    public static void setCallbackProxy(RpcServiceCallbackProxy proxy) {
        RpcClientContextAccessor.proxy = proxy;
    }
}
