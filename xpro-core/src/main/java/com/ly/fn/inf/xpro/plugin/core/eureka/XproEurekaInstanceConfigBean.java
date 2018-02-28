package com.ly.fn.inf.xpro.plugin.core.eureka;

import com.ly.fn.inf.xpro.plugin.core.consts.XproConstants;
import lombok.Setter;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author Mitsui
 * @since 2017年11月28日
 */
public class XproEurekaInstanceConfigBean extends EurekaInstanceConfigBean {
    @Setter
    private PropertyResolver propertyResolver;

    public XproEurekaInstanceConfigBean(InetUtils inetUtils) {
        super(inetUtils);
    }

    @Override
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        final String appName = propertyResolver.getProperty("app.name");
        if (StringUtils.hasText(appName)) {
            super.setAppname(appName);
            super.setVirtualHostName(appName);
            super.setSecureVirtualHostName(appName);
        }

        final String gatewayName = propertyResolver.getProperty("gateway.name");
        if (StringUtils.hasText(gatewayName)) {
            Map<String, String> metadata = super.getMetadataMap();
            metadata.put(XproConstants.XPRO_GATEWAY_NAME, gatewayName);
        }
    }
}
