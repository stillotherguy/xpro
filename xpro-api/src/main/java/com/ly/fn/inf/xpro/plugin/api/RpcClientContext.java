package com.ly.fn.inf.xpro.plugin.api;

/**
 * Rpc客户端上下文类接口定义。
 * 
 * 
 */
public interface RpcClientContext {
    /**
     * 获得最近一次调用的服务器信息
     * 
     * @return
     */
    String getLastCallServerInfo();
}
