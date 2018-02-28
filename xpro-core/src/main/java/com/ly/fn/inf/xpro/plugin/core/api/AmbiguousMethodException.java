package com.ly.fn.inf.xpro.plugin.core.api;

/**
 * 歧义的方法异常类。
 */
public class AmbiguousMethodException extends RuntimeException {
    private static final long serialVersionUID = 7542052557362332118L;

    public AmbiguousMethodException() {}

    public AmbiguousMethodException(String msg) {
        super(msg);
    }
}
