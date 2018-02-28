package com.ly.fn.inf.xpro.plugin.core.client;


import com.ly.fn.inf.xpro.plugin.api.AsyncCallResult;

/**
 * 异步调用结果持有类。
 */
public class AsyncCallResultHolder {
    private AsyncCallResult asyncCallResult;
    private long timeout;

    public AsyncCallResult getAsyncCallResult() {
        return asyncCallResult;
    }

    public void setAsyncCallResult(AsyncCallResult asyncCallResult) {
        this.asyncCallResult = asyncCallResult;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
