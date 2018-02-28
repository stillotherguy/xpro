package com.ly.fn.inf.xpro.plugin.autoconfig.env;

import com.google.common.base.Charsets;
import com.ly.fn.inf.config.ctx.config.ConfigHome;
import com.ly.fn.inf.util.FileUtil;
import com.ly.fn.inf.xpro.plugin.core.server.AppInfo;
import com.ly.fn.inf.xpro.plugin.core.server.ServerPortAllocator;
import com.ly.fn.inf.xpro.plugin.core.util.ApplicationUtils;
import com.ly.fn.inf.xpro.plugin.core.util.NetUtils;
import com.netflix.config.DeploymentContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.ly.fn.inf.xpro.plugin.core.util.XproEnvironmentConstants.*;

/**
 * @author Mitsui
 * @since 2017年11月28日
 */
public class XproEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        environment.acceptsProfiles("default", "dev", "integration", "inte", "igr", "rc", "prod");
        
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        String appName = ApplicationUtils.getAppName(classLoader);
        String appVersion = ApplicationUtils.getAppVersion(classLoader);
        
        String[] activeProfiles = environment.getActiveProfiles();
        if (ArrayUtils.isEmpty(activeProfiles)) {
            String env = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
            if (StringUtils.isNotBlank(env)) {
                activeProfiles = new String[] {env};
            }
        }
        if (ArrayUtils.isEmpty(activeProfiles)) {
            String env = System.getenv(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
            if (StringUtils.isNotBlank(env)) {
                activeProfiles = new String[] {env};
            }
        }
        String activeProfile = ArrayUtils.isEmpty(activeProfiles) ? "default" : activeProfiles[0];
        
        if (StringUtils.equals("default", activeProfile)) {
            File envFile = new File(ConfigHome.getDir() + "/" + appName + "/" + "env");
            if (envFile.exists()) {
                activeProfile = FileUtil.readLine(envFile).trim();
            } else {
                System.err.println("environment activeProfile env file: " + envFile + " is not exists.");
            }
            if (StringUtils.equals("integration", activeProfile)) {
                activeProfile = "inte";
            } else if (StringUtils.equals("igr", activeProfile)) {
                activeProfile = "inte";
            }
            
            if (StringUtils.equals("default", activeProfile) == false) {
                environment.setActiveProfiles(activeProfile);
                System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, activeProfile);
            }
        }
        
        System.out.println("environment activeProfile: " + activeProfile);
        System.out.println("environment eureka.cli.serviceUrl.defaultZone: " + environment.getProperty("eureka.cli.serviceUrl.defaultZone"));
        System.out.println("environment spring.boot.admin.url: " + environment.getProperty("spring.boot.admin.url"));
        
        String gatewayName = environment.getProperty("gateway.name");
        String configPortString = environment.getProperty(SERVER_PORT);

        int port = -1;
        if (StringUtils.isNotBlank(configPortString)) {
            port = NumberUtils.toInt(configPortString, -1);
        }

        port = ServerPortAllocator.getInstance().selectPort(port, appName);

        AppInfo appInfo = AppInfo.getInstance();
        appInfo.setPort(port);
        appInfo.setAppName(appName);
        appInfo.setGatewayName(gatewayName);
        appInfo.setAppVersion(appVersion);

        System.setProperty(DeploymentContext.ContextKey.appId.getKey(), appName);
        System.setProperty(DeploymentContext.ContextKey.environment.getKey(), activeProfile);
        System.setProperty(DeploymentContext.ContextKey.serverId.getKey(), appName);
        System.setProperty(DeploymentContext.ContextKey.datacenter.getKey(), appName);
        
        String ip = NetUtils.getLocalHost();
        System.out.println("environment ip: " + ip);

        Map<String, Object> xproProperties = new HashMap<String, Object>();
        xproProperties.put(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, activeProfile);
        xproProperties.put(INFO_APP_NAME, appName);
        xproProperties.put(GATEWAY_NAME, gatewayName);
        xproProperties.put(INFO_APP_VERSION, appVersion);
        xproProperties.put(SPRING_APPLICATION_NAME, appName);

        xproProperties.put(SERVER_PORT, port + "");
        
        xproProperties.put("spring.application.name", appName);
        
        xproProperties.put("eureka.instance.ip-address", ip);
        xproProperties.put("eureka.instance.prefer-ip-address", "true");

        xproProperties.put(RIBBON_EAGER_LOAD_ENABLED, "true");
        xproProperties.put(RIBBON_EAGER_LOAD_CLIENTS, appName);
        xproProperties.put(RIBBON_CONNECT_TIMEOUT, 5000);
        xproProperties.put(RIBBON_READ_TIMEOUT, 30000);

        xproProperties.put(SPRING_HTTP_ENCODING_FORCE, "true");

        xproProperties.put(EUREKA_CLIENT_HEALTHCHECK_ENABLED, StringUtils.defaultIfBlank(environment.getProperty(EUREKA_CLIENT_HEALTHCHECK_ENABLED), "true"));
        xproProperties.put(EUREKA_CLIENT_REGISTRY_FETCH_INTERVAL_SECONDS, StringUtils.defaultIfBlank(environment.getProperty(EUREKA_CLIENT_REGISTRY_FETCH_INTERVAL_SECONDS), "30"));
        xproProperties.put(EUREKA_INSTANCE_PREFER_IP_ADDRESS, StringUtils.defaultIfBlank(environment.getProperty(EUREKA_INSTANCE_PREFER_IP_ADDRESS), "true"));

        xproProperties.put(EUREKA_CLIENT_REGISTER_WITH_EUREKA, StringUtils.defaultIfBlank(environment.getProperty(EUREKA_CLIENT_REGISTER_WITH_EUREKA), "true"));
        xproProperties.put(EUREKA_CLIENT_FETCH_REGISTRY, StringUtils.defaultIfBlank(environment.getProperty(EUREKA_CLIENT_FETCH_REGISTRY), "true"));
        xproProperties.put(FEIGN_HYSTRIX_ENABLED, StringUtils.defaultIfBlank(environment.getProperty(FEIGN_HYSTRIX_ENABLED), "false"));

        xproProperties.put(LOGGING_LEVEL_ROOT, StringUtils.defaultIfBlank(environment.getProperty(LOGGING_LEVEL_ROOT), "INFO"));
        xproProperties.put(INFO_APP_ENCODING, StringUtils.defaultIfBlank(environment.getProperty(INFO_APP_ENCODING), Charsets.UTF_8.name()));
        xproProperties.put(ENDPOINTS_SHUTDOWN_ENABLED, StringUtils.defaultIfBlank(environment.getProperty(ENDPOINTS_SHUTDOWN_ENABLED), "true"));
        xproProperties.put(MANAGEMENT_SECURITY_ENABLED, StringUtils.defaultIfBlank(environment.getProperty(MANAGEMENT_SECURITY_ENABLED), "false"));
//        String ip = environment.getProperty(SPRING_CLOUD_CLIENT_IP);
//        if (ip != null) {
        xproProperties.put(EUREKA_INSTANCE_INSTANCE_ID, ip + ":" + port);
//        }
        
        xproProperties.put("security.basic.enabled", StringUtils.defaultIfBlank(environment.getProperty("security.basic.enabled"), "false"));
        xproProperties.put("management.security.enabled", StringUtils.defaultIfBlank(environment.getProperty("management.security.enabled"), "false"));
        xproProperties.put("server.compression.enabled", StringUtils.defaultIfBlank(environment.getProperty("server.compression.enabled"), "true"));

        xproProperties.put("server.undertow.accesslog.dir", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.accesslog.dir"), "/log/java/" + appName + "/accesslog"));
        xproProperties.put("server.undertow.accesslog.enabled", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.accesslog.enabled"), "true"));
        xproProperties.put("server.undertow.accesslog.pattern", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.accesslog.pattern"), "%t %a \"%r\" %s (%D ms)"));
        xproProperties.put("server.undertow.accesslog.prefix", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.accesslog.prefix"), "access_log."));
        xproProperties.put("server.undertow.accesslog.rotate", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.accesslog.rotate"), "true"));
        xproProperties.put("server.undertow.accesslog.suffix", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.accesslog.suffix"), "log"));
        
        xproProperties.put("server.undertow.buffer-size", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.buffer-size"), "4096"));
        xproProperties.put("server.undertow.buffers-per-region", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.buffers-per-region"), "4096"));
        xproProperties.put("server.undertow.direct-buffers", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.direct-buffers"), "true"));
        xproProperties.put("server.undertow.max-http-post-size", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.max-http-post-size"), "4096"));
        
        xproProperties.put("server.undertow.io-threads", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.io-threads"), "10"));
        xproProperties.put("server.undertow.worker-threads", StringUtils.defaultIfBlank(environment.getProperty("server.undertow.worker-threads"), "20"));

        environment.getPropertySources().addFirst(new MapPropertySource("XproProperties", xproProperties));
    }
}
