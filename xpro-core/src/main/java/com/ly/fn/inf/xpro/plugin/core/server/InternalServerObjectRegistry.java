package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.xpro.plugin.core.api.CallbackDescription;
import com.ly.fn.inf.xpro.plugin.core.api.ServerObjectRegistry;

/**
 * 服务端对象注册器接口定义类。
 */
public interface InternalServerObjectRegistry extends ServerObjectRegistry {
    /**
     * 注册回调对象
     * 
     * @return 回调服务编号
     */
    String registerCallbackObject(Object targetObject, Class<?> infClazz, CallbackDescription callbackDesc);

    /**
     * 更新回调对象，用于CallbackObject的超时时限复位计数。
     * 
     * @return true表示touch CallbackObject成功，false表示找不到Object.
     */
    //boolean touchCallbackObject(String serviceId);

    /**
     * 解注回调对象
     * 
     * @return true表示touch CallbackObject成功，false表示找不到Object.
     */
    boolean unregisterCallbackObject(String serviceId);

    /**
     * 启动服务对象注册器，在服务对象注册好之后。
     */
    void start();

    /**
     * 准备关闭
     */
    void prepareStop();

    /**
     * 关闭服务对象注册器
     */
    void stop();

    ServiceObjectFinder getServiceObjectFinder();
}
