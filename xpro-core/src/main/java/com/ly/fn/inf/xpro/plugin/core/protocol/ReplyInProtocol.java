package com.ly.fn.inf.xpro.plugin.core.protocol;

import java.lang.reflect.Method;

/**
 * Rpc服务应答协议输入转换器接口定义类。
 */
public interface ReplyInProtocol {
    /**
     * 读取头对象
     */
    ReplyHeader readHeader();

    /**
     * 读取返回对象
     */
    Object readRetObject(Method method);

    /**
     * 读取异常对象
     */
    Throwable readException();

    /**
     * 获得Transport层的消息对象
     */
    Message getMessage();
}
