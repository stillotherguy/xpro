package com.ly.fn.inf.xpro.plugin.core.protocol;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Rpc服务应答协议输出转换器接口定义类。
 */
public interface ReplyOutProtocol {
    /**
     * 写入应答头。
     */
    void writeHeader(ReplyHeader header);

    /**
     * 写入返回对象。
     */
    void writeRetObject(Method method, Type retType, Object retObj);

    /**
     * 写入异常对象。
     */
    void writeException(Throwable e);

    /**
     * 获得Transport层的消息对象。
     */
    Message getMessage();

    /**
     * 复位协议
     */
    void reset();
}
