package com.ly.fn.inf.xpro.plugin.autoconfig;

import com.ly.fn.inf.xpro.plugin.autoconfig.annotation.XproPluginApplication;
import com.ly.fn.inf.xpro.plugin.core.api.RunMode;
import com.ly.fn.inf.xpro.plugin.core.client.DefaultClientObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.server.DefaultServerObjectRegistry;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @author Mitsui
 * @since 2017年11月17日
 */
public class XproPluginRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition clientBeanDefinition = new GenericBeanDefinition();
        clientBeanDefinition.setBeanClass(DefaultClientObjectFactory.class);
        clientBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

        MutablePropertyValues clientProps = new MutablePropertyValues();
        clientProps.addPropertyValue("ldRestTemplate", new RuntimeBeanReference("ldRestTemplate"));
        clientProps.addPropertyValue("restTemplate", new RuntimeBeanReference("restTemplate"));
        //clientProps.addPropertyValue("xproProperties", new RuntimeBeanReference("xproProperties"));
        clientProps.addPropertyValue("protocolFactorySelector", new RuntimeBeanReference("protocolFactorySelector"));
        clientProps.addPropertyValue("serverObjectRegistry", new RuntimeBeanReference("serverObjectRegistry"));

        clientBeanDefinition.setPropertyValues(clientProps);
        registry.registerBeanDefinition("clientObjectFactory", clientBeanDefinition);

        GenericBeanDefinition serverBeanDefinition = new GenericBeanDefinition();
        serverBeanDefinition.setBeanClass(DefaultServerObjectRegistry.class);
        serverBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

        MutablePropertyValues serverProps = new MutablePropertyValues();
        serverProps.addPropertyValue("clientObjectFactory", new RuntimeBeanReference("clientObjectFactory"));
        serverProps.addPropertyValue("remoteObjectFactory", new RuntimeBeanReference("remoteObjectFactory"));
        serverProps.addPropertyValue("serviceObjectBinder", new RuntimeBeanReference("serviceObjectHolder"));
        serverProps.addPropertyValue("serviceObjectFinder", new RuntimeBeanReference("serviceObjectHolder"));
        serverProps.addPropertyValue("runMode", new RuntimeBeanReference("runMode"));

        serverBeanDefinition.setPropertyValues(serverProps);

        registry.registerBeanDefinition("serverObjectRegistry", serverBeanDefinition);

        GenericBeanDefinition xproBeanPostProcessorBeanDefinition = new GenericBeanDefinition();
        xproBeanPostProcessorBeanDefinition.setBeanClass(XprowiredBeanPostProcessor.class);
        xproBeanPostProcessorBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

        MutablePropertyValues xproBPPProps = new MutablePropertyValues();
        xproBPPProps.addPropertyValue("clientObjectFactory", new RuntimeBeanReference("clientObjectFactory"));

        xproBeanPostProcessorBeanDefinition.setPropertyValues(xproBPPProps);
        registry.registerBeanDefinition(XprowiredBeanPostProcessor.class.getSimpleName(), xproBeanPostProcessorBeanDefinition);

        GenericBeanDefinition xproServerLifecycleBeanDefinition = new GenericBeanDefinition();
        xproServerLifecycleBeanDefinition.setBeanClass(XproServerLifecycle.class);
        xproServerLifecycleBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        registry.registerBeanDefinition(XproServerLifecycle.class.getSimpleName(), xproServerLifecycleBeanDefinition);
        
        GenericBeanDefinition markerBeanDefinition = new GenericBeanDefinition();
        markerBeanDefinition.setBeanClass(Marker.class);
        markerBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        registry.registerBeanDefinition(Marker.class.getSimpleName(), markerBeanDefinition);

        Map<String, Object> attributes = metadata.getAnnotationAttributes(XproPluginApplication.class.getName());

        GenericBeanDefinition runModeBeanDefinition = new GenericBeanDefinition();
        runModeBeanDefinition.setBeanClass(RunMode.class);
        runModeBeanDefinition.setFactoryMethodName("valueOf");

        Object runMode = attributes.get("runMode");
        if (runMode == null)
            runMode = RunMode.SERVER;

        ConstructorArgumentValues runModeValues = new ConstructorArgumentValues();
        runModeValues.addIndexedArgumentValue(0, RunMode.class.getName());
        runModeValues.addIndexedArgumentValue(1, runMode.toString());
        runModeBeanDefinition.setConstructorArgumentValues(runModeValues);
        runModeBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        registry.registerBeanDefinition("runMode", runModeBeanDefinition);
    }

    public static class Marker {}
}
