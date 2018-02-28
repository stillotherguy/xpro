package com.ly.fn.inf.xpro.plugin.core.api;

/**
 * 丢失标签异常类。
 */
public class MissingAnnotationException extends RuntimeException {
    private static final long serialVersionUID = 6278280769399372899L;

    public MissingAnnotationException() {}

    public MissingAnnotationException(String msg) {
        super(msg);
    }
}
