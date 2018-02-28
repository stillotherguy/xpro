package com.ly.fn.inf.xpro.plugin.core.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Rpc服务请求头类。
 */
public class RequestHeader {
    /**
     * Rpc版本号
     */
    private String version = XproVersion.CURRENT_VER;

    /**
     * Rpc服务编号
     */
    private String serviceId;

    /**
     * Rpc服务组
     */
    private String serviceGroup;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 属性
     */
    private Map<String, String> props;

    /**
     * 参数数量
     */
    private int argsCount;

    /**
     * 参数签名
     */
    private String argsSignature;

    /**
     * 本次调用的超时时间
     */
    private Long timeout;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 网关名称
     */
    private String gatewayName;

    /**
     * 跨越安全区域标志，需要进行安全处理。
     */
    private Boolean crossDMZFlag;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> properties) {
        this.props = properties;
    }

    public void setProp(String name, String value) {
        if (this.props == null) {
            this.props = new HashMap<String, String>();
        }

        this.props.put(name, value);
    }

    public String getProp(String name) {
        if (this.props == null) {
            return null;
        }

        return this.props.get(name);
    }

    public int getArgsCount() {
        return argsCount;
    }

    public void setArgsCount(int argsCount) {
        this.argsCount = argsCount;
    }

    public String getArgsSignature() {
        return argsSignature;
    }

    public void setArgsSignature(String argsSignature) {
        this.argsSignature = argsSignature;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public Boolean getCrossDMZFlag() {
        return crossDMZFlag;
    }

    public void setCrossDMZFlag(Boolean crossDMZFlag) {
        this.crossDMZFlag = crossDMZFlag;
    }
}
