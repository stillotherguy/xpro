package com.ly.fn.inf.xpro.plugin.core.protocol;

/**
 * 协议工厂选择器接口定义类。
 */
public interface ProtocolFactorySelector {
    /**
     * 获得内容类型对应的协议工厂
     */
    ProtocolFactory select(String contentType);
}
