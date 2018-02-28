package com.ly.fn.inf.xpro.plugin.core.api;

import com.ly.fn.inf.rpc.annotaion.RpcProperty;

import java.util.List;


/**
 * Rpc服务方法参数描述类。
 */
public class ServiceMethodParameterDescription {
    /**
     * 消息参数映射列表
     */
    private List<RpcProperty> messageProperties;

    /**
     * 回呼标签
     */
    private CallbackDescription callback;

    public List<RpcProperty> getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(List<RpcProperty> messageProperties) {
        this.messageProperties = messageProperties;
    }

    public CallbackDescription getCallback() {
        return callback;
    }

    public void setCallback(CallbackDescription callback) {
        this.callback = callback;
    }

}
