package com.ly.fn.inf.xpro.plugin.autoconfig;

import com.ly.fn.inf.xpro.plugin.api.RemoteObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.XproProperties;
import com.ly.fn.inf.xpro.plugin.core.api.ClientObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.api.RunMode;
import com.ly.fn.inf.xpro.plugin.core.client.DefaultRemoteObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.endpoint.ServerController;
import com.ly.fn.inf.xpro.plugin.core.eureka.XproEurekaInstanceConfigBean;
import com.ly.fn.inf.xpro.plugin.core.protocol.*;
import com.ly.fn.inf.xpro.plugin.core.server.AppInfo;
import com.ly.fn.inf.xpro.plugin.core.server.DefaultServiceObjectHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.cloud.client.loadbalancer.AsyncLoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventListener;
import java.util.List;

/**
 * @author Mitsui
 * @since 2017年1月10日
 */
@Slf4j
@Configuration
@ConditionalOnBean(XproPluginRegistrar.Marker.class)
@EnableConfigurationProperties(XproProperties.class)
@AutoConfigureAfter({LoadBalancerAutoConfiguration.class, AsyncLoadBalancerAutoConfiguration.class})
public class XproAutoConfiguration implements EnvironmentAware, InitializingBean {

    @Autowired
    private XproProperties xproProperties;

    private ConfigurableEnvironment env;

    private PropertyResolver propertyResolver;

    @Bean
    public DefaultServiceObjectHolder serviceObjectHolder() {
        return new DefaultServiceObjectHolder();
    }

    /*@Bean
    public IRule p2pFilteringRule() { 
        return new P2PFilteringRule();
    }*/

    @Bean
    public RemoteObjectFactory remoteObjectFactory(ClientObjectFactory clientObjectFactory) {
        return new DefaultRemoteObjectFactory(clientObjectFactory);
    }

    @Bean
    public ServerController serverController() {
        return new ServerController();
    }

    @Bean
    @ConditionalOnWebApplication
    public ServletListenerRegistrationBean<EventListener> servletListenerRegistrationBean() {
        ServletListenerRegistrationBean<EventListener> registrationBean = new ServletListenerRegistrationBean<EventListener>();
        registrationBean.setListener(requestContextListener());
        return registrationBean;
    }

    @Bean
    @ConditionalOnWebApplication
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public ProtocolFactory jacksonProtocolFactory() {
        return new DefaultProtocolFactory(MessageContentTypes.JSON_JACKSON);
    }

    @Bean
    public ProtocolFactory jacksonSmileProtocolFactory() {
        return new DefaultProtocolFactory(MessageContentTypes.JSON_JACKSON_SMILE);
    }

    @Bean
    public ProtocolFactorySelector protocolFactorySelector(List<ProtocolFactory> factoryList) {
        DefaultProtocolFactorySelector selector = new DefaultProtocolFactorySelector();
        selector.setProtocolFactories(factoryList);
        return selector;
    }

    @Bean
    public EurekaClientConfigBean eurekaClientConfigBean(RunMode runMode) {
        EurekaClientConfigBean client = new EurekaClientConfigBean();
        if ("bootstrap".equals(propertyResolver.getProperty("spring.config.name")) || RunMode.CLIENT.equals(runMode)) {
            // We don't register during bootstrap by default, but there will be another
            // chance later.
            client.setRegisterWithEureka(false);
        }

        return client;
    }

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) throws MalformedURLException {
        PropertyResolver eurekaPropertyResolver = new RelaxedPropertyResolver(this.env, "eureka.instance.");
        String hostname = eurekaPropertyResolver.getProperty("hostname");

        boolean preferIpAddress = Boolean.parseBoolean(eurekaPropertyResolver.getProperty("preferIpAddress"));
        int nonSecurePort = Integer.valueOf(propertyResolver.getProperty("server.port", propertyResolver.getProperty("port", "8080")));
        int managementPort = Integer.valueOf(propertyResolver.getProperty("management.port", String.valueOf(nonSecurePort)));
        String managementContextPath = propertyResolver.getProperty("management.contextPath", propertyResolver.getProperty("server.contextPath", "/"));
        XproEurekaInstanceConfigBean instance = new XproEurekaInstanceConfigBean(inetUtils);
        instance.setPropertyResolver(propertyResolver);
        instance.setNonSecurePort(nonSecurePort);
        instance.setInstanceId(IdUtils.getDefaultInstanceId(propertyResolver));
        instance.setPreferIpAddress(preferIpAddress);
        if (managementPort != nonSecurePort && managementPort != 0) {
            if (StringUtils.hasText(hostname)) {
                instance.setHostname(hostname);
            }
            String statusPageUrlPath = eurekaPropertyResolver.getProperty("statusPageUrlPath");
            String healthCheckUrlPath = eurekaPropertyResolver.getProperty("healthCheckUrlPath");
            if (!managementContextPath.endsWith("/")) {
                managementContextPath = managementContextPath + "/";
            }
            if (StringUtils.hasText(statusPageUrlPath)) {
                instance.setStatusPageUrlPath(statusPageUrlPath);
            }
            if (StringUtils.hasText(healthCheckUrlPath)) {
                instance.setHealthCheckUrlPath(healthCheckUrlPath);
            }
            String scheme = instance.getSecurePortEnabled() ? "https" : "http";
            URL base = new URL(scheme, instance.getHostname(), managementPort, managementContextPath);
            instance.setStatusPageUrl(new URL(base, StringUtils.trimLeadingCharacter(instance.getStatusPageUrlPath(), '/')).toString());
            instance.setHealthCheckUrl(new URL(base, StringUtils.trimLeadingCharacter(instance.getHealthCheckUrlPath(), '/')).toString());
        }

        AppInfo.getInstance().setHostName(instance.getHostName(false));

        return instance;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = (ConfigurableEnvironment) environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.propertyResolver = new RelaxedPropertyResolver(env);

        log.info("XproProperties properties : {}", xproProperties);
    }
}
