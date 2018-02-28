package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.xpro.plugin.core.api.ServiceClass;
import com.ly.fn.inf.xpro.plugin.core.protocol.ReplyOutProtocol;
import com.ly.fn.inf.xpro.plugin.core.protocol.RequestInProtocol;

import java.lang.reflect.Method;

/**
 * 服务对象接口定义类。
 */
public interface ServiceObject {

    /**
     * 获得网关名称服务编号
     */
    String getGatewayName();

    /**
     * 获得应用名称
     */
    String getAppName();

    /**
     * 获得服务编号
     */
    String getServiceId();

    /**
     * 获得服务组
     */
    String getServiceGroup();

    /**
     * 获得服务接口定义类
     */
    Class<?> getServiceInterface();

    /**
     * 获得服务类
     */
    ServiceClass getServiceClass();

    /**
     * 是否回调对象
     */
    boolean isCallbackObject();

    /**
     * 调用方法处理。
     */
    void invoke(Method method, RequestInProtocol in, ReplyOutProtocol out);
}
