package com.ly.fn.inf.xpro.plugin.core.server;

/**
 * 服务对象绑定器接口定义类。
 */
public interface ServiceObjectBinder {
    /**
     * 绑定服务对象。
     */
    void bind(ServiceObject srvObj);

    /**
     * 解绑服务对象，主要用于CallbackObject的解除绑定。
     * 
     * @return true表示解绑成功，false表示serviceId已经解绑。
     */
    boolean unbind(String serviceId);
}
