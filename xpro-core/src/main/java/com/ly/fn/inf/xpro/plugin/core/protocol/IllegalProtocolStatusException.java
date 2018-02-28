package com.ly.fn.inf.xpro.plugin.core.protocol;

/**
 * 非法的协议状态异常类。
 */
public class IllegalProtocolStatusException extends RuntimeException {
    private static final long serialVersionUID = 73461561218679361L;

    public IllegalProtocolStatusException() {}

    public IllegalProtocolStatusException(String msg) {
        super(msg);
    }
}
