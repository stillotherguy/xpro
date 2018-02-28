package com.ly.fn.inf.xpro.plugin.autoconfig;

import com.ly.fn.inf.rpc.annotaion.Rpcwired;
import com.ly.fn.inf.xpro.plugin.api.RemoteObjectFactoryAware;
import com.ly.fn.inf.xpro.plugin.api.annotation.XproAppConfig;
import com.ly.fn.inf.xpro.plugin.core.client.DefaultRemoteObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.client.InternalClientObjectFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author Mitsui
 * @since 2017年1月10日
 */
@Slf4j
public class XprowiredBeanPostProcessor implements BeanPostProcessor, PriorityOrdered, BeanFactoryAware {
    private ConfigurableListableBeanFactory beanFactory;
    @Setter
    private InternalClientObjectFactory clientObjectFactory;
    private boolean init;

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        if (!init) {
            init = true;
            clientObjectFactory.init();
        }
        if (bean instanceof RemoteObjectFactoryAware) {
            ((RemoteObjectFactoryAware) bean).setRemoteObjectFactory(new DefaultRemoteObjectFactory(clientObjectFactory));
        }
        ReflectionUtils.doWithFields(bean.getClass(), new RpcwiredFieldCallback(clientObjectFactory, bean, beanFactory), new ReflectionUtils.FieldFilter() {
            public boolean matches(Field field) {
                return field.isAnnotationPresent(Rpcwired.class) && field.getType().getPackage().isAnnotationPresent(XproAppConfig.class);
            }
        });
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
