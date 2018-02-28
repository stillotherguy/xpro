package com.ly.fn.inf.xpro.plugin.core.api;

/**
 * 服务端对象注册器接口定义类。
 */
public interface ServerObjectRegistry {
    /**
     * 注册服务对象。
     */
    boolean registerServerObject(Object targetObject);

    /**
     * 注册服务对象。
     */
    boolean registerServerObject(Object targetObject, Class<?> targetClass);
}
