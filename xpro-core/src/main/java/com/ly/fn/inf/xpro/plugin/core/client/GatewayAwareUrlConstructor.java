package com.ly.fn.inf.xpro.plugin.core.client;

import com.ly.fn.inf.xpro.plugin.core.api.ServiceClass;
import com.ly.fn.inf.xpro.plugin.core.api.ServiceMethod;
import com.ly.fn.inf.xpro.plugin.core.consts.XproConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Mitsui
 * @since 2018年01月16日
 */
@Slf4j
public class GatewayAwareUrlConstructor implements UrlConstructor {

    private final ServiceClass serviceClass;

    private final ServiceMethod serviceMethod;

    private final DiscoveryClient discoveryClient;

    public GatewayAwareUrlConstructor(ServiceClass serviceClass, ServiceMethod serviceMethod, DiscoveryClient discoveryClient) {
        this.serviceClass = serviceClass;
        this.serviceMethod = serviceMethod;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public String construct(String gatewayName, String host, boolean isCallback) {
        String serviceGroup = serviceClass.getDescription().getServiceGroup();
        if (serviceGroup == null)
            serviceGroup = "callback";

        final String serviceId = serviceClass.getDescription().getServiceId();
        final String methodName = serviceMethod.getMethod().getName();
        final StringBuilder sb = new StringBuilder("http://");
        if (isCallback)
            return sb.append(host)
                    .append('/')
                    .append(serviceGroup)
                    .append('/')
                    .append(serviceId)
                    .append('/')
                    .append(methodName)
                    .toString();

        String remoteGateway = null;
        try {
            // 找到对应服务的实例
            List<ServiceInstance> instances = discoveryClient.getInstances(host);
            for (ServiceInstance instance : instances) {
                Map<String, String> metadata = instance.getMetadata();
                remoteGateway = metadata.get(XproConstants.XPRO_GATEWAY_NAME);

                if (!StringUtils.isEmpty(remoteGateway))
                    break;
            }

            if (!StringUtils.isEmpty(remoteGateway)) {
                // 如果双方所属网关不同，则走服务端的网关调用
                if (!remoteGateway.equals(gatewayName)) {
                    List<ServiceInstance> gatewayInstances = discoveryClient.getInstances(remoteGateway);
                    // 校验gateway是否可用，不可用则走p2p模式
                    if (!CollectionUtils.isEmpty(gatewayInstances)) {
                        // FIXME 如果gateway一个是http一个是https
                        return sb.append(remoteGateway)
                                .append('/')
                                .append(host)
                                .append('/')
                                .append(serviceGroup)
                                .append('/')
                                .append(serviceId)
                                .append('/')
                                .append(methodName)
                                .toString();
                    }

                    log.info("remote gateway instances for name {} are empty, fallback to normal invocation", remoteGateway);
                } else {
                    log.info("remote gateway and local gateway equals as {}, fallback to normal invocation", remoteGateway);
                }
            }
        } catch (Exception e) {
            log.info("error happened during invoke remote gateway " + remoteGateway + ", fallback to normal invocation", e);
        }

        return sb.append(host)
                .append('/')
                .append(serviceGroup)
                .append('/')
                .append(serviceId)
                .append('/')
                .append(methodName)
                .toString();
    }
}
