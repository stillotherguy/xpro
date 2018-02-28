package com.ly.fn.inf.xpro.plugin.core.protocol;

/**
 * 回调参数类。
 */
public class CallbackArg {
    public static final String CALLBACK_TYPE_ANONYMOUS = "0";
    public static final String CALLBACK_TYPE_LNK_SERVICE = "1";

    /**
     * 回调类型
     */
    private String callbackType;

    /**
     * 网关名称
     */
    private String gatewayName;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 服务编号
     */
    private String serviceId;

    /**
     * 服务编组
     */
    private String serviceGroup;

    /**
     * (用于RpcService回调）服务器地址
     */
    private String serverAddress;

    /**
     * (用于RpcService回调）内容类型
     */
    private String contentType;

    public String getCallbackType() {
        return callbackType;
    }

    public void setCallbackType(String callbackType) {
        this.callbackType = callbackType;
    }

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

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
