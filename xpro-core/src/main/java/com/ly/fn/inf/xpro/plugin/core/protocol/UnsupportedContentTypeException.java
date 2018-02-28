package com.ly.fn.inf.xpro.plugin.core.protocol;

/**
 * 不支持的内容类型异常类。
 */
public class UnsupportedContentTypeException extends RuntimeException {
    private static final long serialVersionUID = -2502347589532738766L;

    public UnsupportedContentTypeException() {}

    public UnsupportedContentTypeException(String contentType) {
        super("Unsupported contentType=[" + contentType + "].");
    }
}
