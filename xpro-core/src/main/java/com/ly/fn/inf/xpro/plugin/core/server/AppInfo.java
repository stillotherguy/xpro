package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.xpro.plugin.core.util.IdUtils;
import lombok.Data;

/**
 * @author Mitsui
 * @since 2018年01月25日
 */
@Data
public class AppInfo {

    private String appName;

    private String gatewayName;

    private String appVersion;

    private String hostName;

    private int port;

    private static volatile AppInfo INSTANCE = new AppInfo();

    public static AppInfo getInstance() {
        if (INSTANCE == null) {
            synchronized (AppInfo.class) {
                if (INSTANCE == null)
                    INSTANCE = new AppInfo();
            }
        }

        return INSTANCE;
    }

    private AppInfo() {
    }

    public String toAddress() {
        return IdUtils.getDefaultInstanceId(hostName, port);
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }
}
