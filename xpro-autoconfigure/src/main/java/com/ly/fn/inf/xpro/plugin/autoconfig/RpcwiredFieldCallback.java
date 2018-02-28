package com.ly.fn.inf.xpro.plugin.autoconfig;

import java.lang.reflect.Field;

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.ly.fn.inf.rpc.annotaion.Rpcwired;
import com.ly.fn.inf.xpro.plugin.core.api.ClientObjectFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mitsui
 * @since 2017年1月10日
 */
@Slf4j
public class RpcwiredFieldCallback implements FieldCallback {
    private final Object bean;
    private final ClientObjectFactory clientObjectFactory;
    private final ConfigurableListableBeanFactory beanFactory;

    public RpcwiredFieldCallback(ClientObjectFactory clientObjectFactory, Object bean, ConfigurableListableBeanFactory beanFactory) {
        super();
        this.clientObjectFactory = clientObjectFactory;
        this.bean = bean;
        this.beanFactory = beanFactory;
    }

    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        ConfigurablePropertyAccessor configurablePropertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(bean);
        String name = field.getName();
        Class<?> serviceInterface = field.getType();
//        Object fieldValue = configurablePropertyAccessor.getPropertyValue(name);
//        if (fieldValue != null) {
//            return;
//        }
        Rpcwired rpcwired = field.getAnnotation(Rpcwired.class);
        if (rpcwired == null) {
            return;
        }
        if (rpcwired.localInjectFirstly()) {
            try {
                Object srvBean = beanFactory.getBean(serviceInterface);
                if (srvBean != null) {
                    configurablePropertyAccessor.setPropertyValue(name, srvBean);
                    log.info("found local bean, serviceInterface=[" + serviceInterface + "].");
                    return;
                }
            } catch (Throwable e) {}
        }
        configurablePropertyAccessor.setPropertyValue(name, clientObjectFactory.getClientObject(serviceInterface));
        log.info("wired INF-XPRO object: {}.{}", bean.getClass().getName(), name);
        
    }
}
