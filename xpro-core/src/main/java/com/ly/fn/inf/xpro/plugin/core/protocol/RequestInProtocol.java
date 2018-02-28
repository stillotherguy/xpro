package com.ly.fn.inf.xpro.plugin.core.protocol;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * RpcService的请求协议输入转换器接口定义类。
 */
public interface RequestInProtocol {
    /**
     * 读取Rpc服务请求头
     */
    RequestHeader readHeader();

    /**
     * 读取Rpc服务参数
     */
    Object[] readArgs(Method method);

    /**
     * 获得Transport层的消息对象
     */
    Message getMessage();

    /**
     * 获得消息属性集
     */
    Map<String, String> getMessageProperties();
}
