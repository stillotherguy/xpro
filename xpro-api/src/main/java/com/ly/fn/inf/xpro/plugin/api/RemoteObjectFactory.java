package com.ly.fn.inf.xpro.plugin.api;

/**
 * 远程Stub工厂接口定义类。
 */
public interface RemoteObjectFactory {
    /**
     * 获得Stub序列化数据对应的远程对象
     */
    <T> T getRemoteObject(String stubSerializeData, Class<T> infClazz);

    /**
     * 获得指定接口和serviceId、serviceGroup对应的远程对象
     */
    <T> T getRemoteObject(Class<T> infClazz, String serviceId, String serviceGroup);
}
