package com.ly.fn.inf.xpro.plugin.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2017年12月8日 下午9:46:08
 */
@Slf4j
public class ApplicationUtils {
    
    public static String getAppName(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }
        String appName = System.getProperty(XproEnvironmentConstants.APPLICATION_NAME);
        log.info("find appName : {} from System Property.", appName);
        if (StringUtils.isBlank(appName)) {
            appName = System.getenv(XproEnvironmentConstants.APPLICATION_NAME);
            log.info("find appName : {} from System env.", appName);
        }
        if (StringUtils.isBlank(appName)) {
            Properties props = new Properties();
            try {
                InputStream in = (classLoader != null ? classLoader.getResourceAsStream(XproEnvironmentConstants.APP_CONFIG_FILE)
                        : ClassLoader.getSystemResourceAsStream(XproEnvironmentConstants.APP_CONFIG_FILE));
                props.load(in);
                appName = props.getProperty(XproEnvironmentConstants.APPLICATION_NAME);
                log.info("find appName : {} from config file : {}.", appName, XproEnvironmentConstants.APP_CONFIG_FILE);
            } catch (Throwable e) {
                log.error("find appName from " + XproEnvironmentConstants.APP_CONFIG_FILE + " Error.", e);
            }
        }
        return appName;
    }
    
    public static String getAppVersion(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }
        String appVersion = System.getProperty(XproEnvironmentConstants.APPLICATION_VERSION);
        log.info("find appVersion : {} from System Property.", appVersion);
        if (StringUtils.isBlank(appVersion)) {
            appVersion = System.getenv(XproEnvironmentConstants.APPLICATION_VERSION);
            log.info("find appVersion : {} from System env.", appVersion);
        }
        if (StringUtils.isBlank(appVersion)) {
            Properties props = new Properties();
            try {
                InputStream in = (classLoader != null ? classLoader.getResourceAsStream(XproEnvironmentConstants.APP_CONFIG_FILE)
                        : ClassLoader.getSystemResourceAsStream(XproEnvironmentConstants.APP_CONFIG_FILE));
                props.load(in);
                appVersion = props.getProperty(XproEnvironmentConstants.APPLICATION_VERSION);
                log.info("find appVersion : {} from config file : {}.", appVersion, XproEnvironmentConstants.APP_CONFIG_FILE);
            } catch (Throwable e) {
                log.error("find appVersion from " + XproEnvironmentConstants.APP_CONFIG_FILE + " Error.", e);
            }
        }
        return appVersion;
    }
}
