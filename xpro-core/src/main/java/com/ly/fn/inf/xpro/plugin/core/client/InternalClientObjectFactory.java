package com.ly.fn.inf.xpro.plugin.core.client;

import com.ly.fn.inf.xpro.plugin.core.api.ClientObjectFactory;

/**
 * 客户端对象工厂接口定义类。
 */
public interface InternalClientObjectFactory extends ClientObjectFactory {
    /**
     * 获得抽象接口对应的客户端回调对象
     */
    <T> T getClientObjectForCallback(String gatewayName, String appName, String serviceId, String serviceGroup, String callbackServerInfo, String contentType, Class<T> inf, boolean callbackObj);

    /**
     * 获得Stub序列化数据对应的客户端对象
     */
    <T> T getClientObject(String stubSerializeData, Class<T> infClazz);

    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();
}
