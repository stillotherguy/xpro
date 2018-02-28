package com.ly.fn.inf.xpro.plugin.core.util;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2017年11月29日 下午2:13:41
 */
public interface XproEnvironmentConstants {
    String MANAGEMENT_SECURITY_ENABLED = "management.security.enabled";
    String ENDPOINTS_SHUTDOWN_ENABLED = "endpoints.shutdown.enabled";
    String INFO_APP_ENCODING = "info.app.encoding";
    String LOGGING_LEVEL_ROOT = "logging.level.root";
    String FEIGN_HYSTRIX_ENABLED = "feign.hystrix.enabled";
    String EUREKA_CLIENT_FETCH_REGISTRY = "eureka.invoke.fetchRegistry";
    String EUREKA_CLIENT_REGISTER_WITH_EUREKA = "eureka.invoke.registerWithEureka";
    String EUREKA_INSTANCE_PREFER_IP_ADDRESS = "eureka.instance.preferIpAddress";
    String EUREKA_INSTANCE_INSTANCE_ID = "eureka.instance.instance_id";
    String EUREKA_CLIENT_REGISTRY_FETCH_INTERVAL_SECONDS = "eureka.invoke.registryFetchIntervalSeconds";
    String EUREKA_CLIENT_HEALTHCHECK_ENABLED = "eureka.invoke.healthcheck.enabled";
    String SERVER_PORT = "server.port";
    String INFO_APP_NAME = "info.app.name";
    String INFO_APP_VERSION = "info.version";
    String SPRING_APPLICATION_NAME = "spring.application.name";
    String SPRING_CLOUD_CLIENT_IP = "spring.cloud.cli.ipAddress";

    String RIBBON_EAGER_LOAD_CLIENTS = "ribbon.eager-load.clients";
    String RIBBON_EAGER_LOAD_ENABLED = "ribbon.eager-load.enabled";
    String RIBBON_CONNECT_TIMEOUT = "ribbon.ConnectTimeout";
    String RIBBON_READ_TIMEOUT = "ribbon.ReadTimeout";

    String SPRING_HTTP_ENCODING_FORCE = "spring.http.encoding.force";
    
    String APP_CONFIG_FILE = "META-INF/xpro.props";
    String APPLICATION_NAME = "app.name";
    
    String GATEWAY_NAME = "gateway.name";

    String APPLICATION_VERSION = "app.version";
    
    String ALLOC_PORT_HOME = "xpro.alloc.port.home";
//    String APP_INSTANCE = "app.instance";
    String APP_INSTANCE = "ins.num";
}
