package com.ly.fn.inf.xpro.plugin.core;

import com.ly.fn.inf.xpro.plugin.core.server.AppInfo;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2017年12月1日 下午5:56:09
 */
@Data
public class RemoteStub implements RemoteObject {
    private static final String XPRO_PROTOCOL = "xpro://";
    private static final Pattern REMOTE_STUB_PATTERN = Pattern.compile("^" + XPRO_PROTOCOL + "(.*),(.*),(.*),(.*),(.*)(.*)$");

    /**
     * 网关名称
     */
    private String gatewayName;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用名称
     */
    private String serverAddress;

    /**
     * 服务编号
     */
    private String serviceId;

    /**
     * 服务组
     */
    private String serviceGroup;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 回调对象标志
     */
    private boolean callbackObject;

    public RemoteStub() {
        super();
    }

    public RemoteStub(String serializeStub) {
        super();
        this.deserializeStub(serializeStub);
    }
    
    public static boolean match(String serializeStub) {
        try {
            if (StringUtils.isBlank(serializeStub)) {
                return false;
            }
            Matcher matcher = REMOTE_STUB_PATTERN.matcher(serializeStub);
            return matcher.find();
        } catch (Throwable e) {
            return false;
        }
    }
    
    public RemoteStub copy(RemoteStub stub) {
        if (stub == null) {
            return this;
        }
        String serverInfo = stub.getServerAddress();
        if (StringUtils.isNotBlank(serverInfo)) {
            this.setServerAddress(serverInfo);
        }
        return this;
    }
    
    @Override
    public String serializeStub() {
        StringBuffer sb = new StringBuffer(XPRO_PROTOCOL);
        sb.append(gatewayName);
        sb.append(",").append(appName);
        sb.append(",").append(serverAddress);
        sb.append(",").append(serviceId);
        sb.append(",").append(serviceGroup);
        sb.append(",").append(contentType);
        sb.append(",").append(callbackObject);
        return sb.toString();
    }

    public void deserializeStub(String serializeStub) {
        Matcher matcher = REMOTE_STUB_PATTERN.matcher(serializeStub);
        boolean matchFound = matcher.find();
        if (matchFound) {
            this.gatewayName = StringUtils.defaultString(matcher.group(1));
            this.appName = StringUtils.defaultString(matcher.group(2));
            this.serverAddress = StringUtils.defaultString(matcher.group(3));
            this.serviceGroup = StringUtils.defaultString(matcher.group(4));
            this.contentType = StringUtils.defaultString(matcher.group(4));
            this.callbackObject = BooleanUtils.toBoolean(matcher.group(4));
            return;
        }
        throw new RuntimeException("Illegal serialize data for RemoteObject.");
    }
    
    public synchronized RemoteStub build() {
        if (StringUtils.isNotBlank(this.getServerAddress())) {
            return this;
        }

        this.setServerAddress(AppInfo.getInstance().toAddress());
        return this;
    }

    @Override
    public String toString() {
        return serializeStub();
    }
}
