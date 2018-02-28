package com.ly.fn.inf.xpro.plugin.api;

/**
 * 异步调用结果接口定义类。
 * 
 * 
 */
public interface AsyncCallResult {
    /**
     * 成功呼叫处理
     * 
     * @param args
     * @param retObject
     */
    void onSuccess(Object[] args, Object retObject);

    /**
     * 异常错误处理
     * 
     * @param args
     * @param e
     */
    void onException(Object[] args, Throwable e);
}
