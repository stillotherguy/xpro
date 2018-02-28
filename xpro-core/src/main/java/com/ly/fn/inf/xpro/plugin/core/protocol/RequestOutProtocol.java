package com.ly.fn.inf.xpro.plugin.core.protocol;

import java.lang.reflect.Method;

/**
 * RpcService的请求协议输出转换器接口定义类。
 */
public interface RequestOutProtocol {
    /**
     * 写Rpc服务请求头
     */
    void writeHeader(RequestHeader header);

    /**
     * 写Rpc服务请求参与
     */
    void writeArgs(Method method, Object[] args);

    /**
     * 获得Transport层的消息对象
     */
    Message getMessage();
}
