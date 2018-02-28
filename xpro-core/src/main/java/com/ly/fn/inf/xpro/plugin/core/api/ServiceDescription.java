package com.ly.fn.inf.xpro.plugin.core.api;

import com.ly.fn.inf.rpc.annotaion.Lifecycle;
import com.ly.fn.inf.rpc.annotaion.Sla;

/**
 * Rpc服务描述类。
 */
public class ServiceDescription {

    /**
     * 服务编号
     */
    private String gatewayName;

    /**
     * 服务编号
     */
    private String appName;

    /**
     * 服务编号
     */
    private String serviceId;

    /**
     * 服务组
     */
    private String serviceGroup;

    /**
     * Sla参数，超时时间（毫秒）
     */
    private long timeout = Sla.DEFAULT_TIMEOUT;

    /**
     * Sla参数，平均处理时长（毫秒）
     */
    private long avgTime = Sla.DEFAULT_AVG_TIME;

    /**
     * Sla参数，优先级别
     */
    private int priority = Sla.DEFAULT_PRIORITY;

    /**
     * 回调对象
     */
    private boolean callbackObject;

    /**
     * 回调生命周期，只有callbackObject=true时候，有效。
     */
    private Lifecycle callbackLifecycle;

    /**
     * 回调生存周期（秒），只有callbackObject=true时候，有效。
     */
    private int callbackTtl;

    /**
     * 回调地址，只有callback时候，有效。
     */
    private String callbackAddress;

    /**
     * 回调内容类型，只有callback时候，有效。
     */
    private String callbackContentType;

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Lifecycle getCallbackLifecycle() {
        return callbackLifecycle;
    }

    public void setCallbackLifecycle(Lifecycle callbackLifecycle) {
        this.callbackLifecycle = callbackLifecycle;
    }

    public int getCallbackTtl() {
        return callbackTtl;
    }

    public void setCallbackTtl(int callbackTtl) {
        this.callbackTtl = callbackTtl;
    }

    public boolean isCallbackObject() {
        return callbackObject;
    }

    public void setCallbackObject(boolean callbackObject) {
        this.callbackObject = callbackObject;
    }

    public String getCallbackAddress() {
        return callbackAddress;
    }

    public void setCallbackAddress(String callbackAddress) {
        this.callbackAddress = callbackAddress;
    }

    public String getCallbackContentType() {
        return callbackContentType;
    }

    public void setCallbackContentType(String callbackContentType) {
        this.callbackContentType = callbackContentType;
    }

    public long getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(long avgTime) {
        this.avgTime = avgTime;
    }

}
