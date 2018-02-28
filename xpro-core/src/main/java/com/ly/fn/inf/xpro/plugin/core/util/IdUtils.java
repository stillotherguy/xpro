package com.ly.fn.inf.xpro.plugin.core.util;

/**
 * @author Mitsui
 * @since 2017年11月28日
 */
public class IdUtils {
    public static final String DEFAULT_SEPARATOR = ":";

    public static String buildServiceId(String appName, Class<?> serviceInterface) {
        return appName + "." + serviceInterface.getName();
    }

    public static String getDefaultInstanceId(String hostname, int port) {
        return hostname + DEFAULT_SEPARATOR + port;
    }
}
