package com.ly.fn.inf.xpro.plugin.core;

/**
 * 找不到服务异常类。
 */
public class NotFoundServiceException extends RuntimeException {
    private static final long serialVersionUID = 7145931889945400682L;

    public NotFoundServiceException() {}

    public NotFoundServiceException(String serviceId, String method) {
        super("Not found the RpcService, serviceId=[" + serviceId + "], method=[" + method + "].");
    }
}
