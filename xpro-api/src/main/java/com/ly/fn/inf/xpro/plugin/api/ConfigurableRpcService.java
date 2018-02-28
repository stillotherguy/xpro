package com.ly.fn.inf.xpro.plugin.api;

/**
 * 可配置的Rpc服务接口定义类。
 * 
 * 
 */
public interface ConfigurableRpcService {
    /**
     * 获得服务组
     * 
     * @return
     */
    String getServiceGroup();

    /**
     * 获得服务编号
     * 
     * @return
     */
    String getServiceId();
}
