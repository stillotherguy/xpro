package com.ly.fn.inf.xpro.plugin.core.client;

import com.ly.fn.inf.xpro.plugin.api.RpcClientContext;

/**
 * 缺省Rpc服务上下文类。
 * 
 * 
 */
public class DefaultRpcClientContext implements RpcClientContext {
    /**
     * 最近调用的服务器信息
     */
    private String lastCallServerInfo;

    public void setLastCallServerInfo(String serverInfo) {
        lastCallServerInfo = serverInfo;
    }

    public String getLastCallServerInfo() {
        return lastCallServerInfo;
    }

}
