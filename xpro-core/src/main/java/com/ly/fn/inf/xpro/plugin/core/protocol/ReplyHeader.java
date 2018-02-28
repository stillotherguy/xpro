package com.ly.fn.inf.xpro.plugin.core.protocol;


/**
 * Rpc服务应答头定义类。
 */
public class ReplyHeader {
    /**
     * 应答类型：正常返回
     */
    public static final String TYPE_RETURN = "1";

    /**
     * 应答类型：异常
     */
    public static final String TYPE_EXCEPTION = "2";

    /**
     * rpc版本号
     */
    private String version = XproVersion.CURRENT_VER;

    /**
     * 应答类型
     */
    private String type;

    /**
     * 服务器信息
     */
    private String serverInfo;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }
}
