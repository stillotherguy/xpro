package com.ly.fn.inf.xpro.plugin.core.client;


import com.ly.fn.inf.xpro.plugin.api.AsyncCallResult;
import com.ly.fn.inf.xpro.plugin.api.ExpectAsyncCall;

/**
 * 异步调用结果句柄访问类
 */
public class ExpectAsyncCallAccessor extends ExpectAsyncCall {
    public static AsyncCallResultHolder getAsyncCallResult() {
        AsyncCallResult result = ExpectAsyncCall.callResultHolder.get();
        if (result == null) {
            return null;
        }

        AsyncCallResultHolder holder = new AsyncCallResultHolder();
        holder.setAsyncCallResult(result);
        holder.setTimeout(ExpectAsyncCall.timeoutHolder.get());

        ExpectAsyncCall.callResultHolder.remove();
        ExpectAsyncCall.timeoutHolder.remove();

        return holder;
    }
}
