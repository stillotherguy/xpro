package com.ly.fn.inf.xpro.plugin.core.api;

/**
 * 客户端对象工厂接口定义类。
 */
public interface ClientObjectFactory {
    /**
     * 获得服务接口类对应的客户端对象。
     */
    <T> T getClientObject(Class<T> serviceInterface);

    /**
     * 获得Stub序列化数据对应的客户端对象
     */
    <T> T getClientObject(String stubSerializeData, Class<T> infClazz);

    /**
     * 获得指定服务编号和服务组对应的客户端对象
     */
    <T> T getClientObject(Class<T> infClazz, String serviceId, String serviceGroup);
}
