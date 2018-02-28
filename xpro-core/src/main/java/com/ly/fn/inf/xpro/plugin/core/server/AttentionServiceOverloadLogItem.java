package com.ly.fn.inf.xpro.plugin.core.server;

import java.util.Date;

/**
 * 关注服务过载日志项目类。
 */
public class AttentionServiceOverloadLogItem {
    /**
     * 跟踪代码
     */
    private String trackingCode;

    /**
     * 服务器信息
     */
    private String serverInfo;

    /**
     * 调用时间
     */
    private Date callTime;

    /**
     * 过载抛弃时间
     */
    private Date dropTime;

    /**
     * 服务编号
     */
    private String serviceId;

    /**
     * 类名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 参数列表关键值，如：String,Integer
     */
    // private String argsSignature;

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Date getCallTime() {
        return callTime;
    }

    public void setCallTime(Date callTime) {
        this.callTime = callTime;
    }

    public Date getDropTime() {
        return dropTime;
    }

    public void setDropTime(Date dropTime) {
        this.dropTime = dropTime;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /*
     * public String getArgsSignature() { return argsSignature; }
     * 
     * public void setArgsSignature(String argsSignature) { this.argsSignature = argsSignature; }
     */

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }
}
