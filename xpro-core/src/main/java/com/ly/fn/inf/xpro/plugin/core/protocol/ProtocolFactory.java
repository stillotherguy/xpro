package com.ly.fn.inf.xpro.plugin.core.protocol;

/**
 * 协议转换器工厂类。
 */
public interface ProtocolFactory {
    /**
     * 获得支持的内容类型
     */
    String supportContentType();

    /**
     * 生成一个请求输入协议转换器。
     */
    RequestInProtocol newRequestInProtocol(Message msg);

    /**
     * 生成一个请求输出协议转换器。
     */
    RequestOutProtocol newRequestOutProtocol();

    /**
     * 生成一个应答输入协议转换器。
     */
    ReplyInProtocol newReplyInProtocol(Message msg);

    /**
     * 生成一个请求输出协议转换器。
     */
    ReplyOutProtocol newReplyOutProtocol(String contentType);
}
