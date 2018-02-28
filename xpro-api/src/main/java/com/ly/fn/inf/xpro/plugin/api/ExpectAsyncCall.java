package com.ly.fn.inf.xpro.plugin.api;

/**
 * 异步调用结果注册器类。
 * 
 * @author
 */
public class ExpectAsyncCall {
    protected static ThreadLocal<AsyncCallResult> callResultHolder = new ThreadLocal<AsyncCallResult>();

    protected static ThreadLocal<Long> timeoutHolder = new ThreadLocal<Long>();

    /**
     * 期望获得后续异步调用的结果
     * 
     * @param result
     * @param timeout 超时时间（毫秒）
     */
    public static void expect(AsyncCallResult result, long timeout) {
        callResultHolder.set(result);
        timeoutHolder.set(timeout);
    }

}
