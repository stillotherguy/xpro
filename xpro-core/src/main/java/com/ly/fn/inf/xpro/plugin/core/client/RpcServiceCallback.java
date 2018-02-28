package com.ly.fn.inf.xpro.plugin.core.client;


import com.ly.fn.inf.xpro.plugin.core.api.ServiceDescription;

/**
 * Rpc服务回调类。
 * 
 * 
 */
public interface RpcServiceCallback {
    /**
     * 获得Rpc服务标签
     * 
     * @return
     */
    ServiceDescription getServiceDescription();
}
