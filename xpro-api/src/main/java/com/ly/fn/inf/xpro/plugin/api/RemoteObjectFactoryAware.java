package com.ly.fn.inf.xpro.plugin.api;

/**
 * 远程对象工厂感知接口定义类。
 */
public interface RemoteObjectFactoryAware {
    /**
     * 设置客户端对象工厂
     */
    void setRemoteObjectFactory(RemoteObjectFactory factory);
}
