package com.ly.fn.inf.xpro.plugin.core.client;

/**
 * @author Mitsui
 * @since 2018年01月16日
 */
public interface UrlConstructor {

    String construct(String gatewayName, String host, boolean isCallback);
}
