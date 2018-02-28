package com.ly.fn.inf.xpro.plugin.core.client;

import com.ly.fn.inf.xpro.plugin.api.RpcServiceCallbackProxy;
import com.ly.fn.inf.xpro.plugin.core.RemoteObject;
import com.ly.fn.inf.xpro.plugin.core.RemoteStub;
import com.ly.fn.inf.xpro.plugin.core.XproProperties;
import com.ly.fn.inf.xpro.plugin.core.api.ServiceClass;
import com.ly.fn.inf.xpro.plugin.core.protocol.ProtocolFactorySelector;
import com.ly.fn.inf.xpro.plugin.core.server.InternalServerObjectRegistry;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mitsui
 * @since 2017年1月10日
 */
@Slf4j
@SuppressWarnings({"unchecked"})
public class DefaultClientObjectFactory implements InternalClientObjectFactory, InitializingBean, BeanFactoryAware, EnvironmentAware {

    /**
     * REST调用
     */
    @Setter
    private RestTemplate restTemplate;

    /**
     * Load Balance REST调用
     */
    @Setter
    private RestTemplate ldRestTemplate;

    /**
     * rpc配置
     */
    @Setter
    private XproProperties xproProperties;

    /**
     * 协议选择器
     */
    @Setter
    private ProtocolFactorySelector protocolFactorySelector;

    /**
     * 服务端对象注册器
     */
    @Setter
    private InternalServerObjectRegistry serverObjectRegistry;

    /**
     * rpc服务回调Proxy
     */
    @Setter
    private RpcServiceCallbackProxy rpcServiceCallbackProxy;

    private ConfigurableBeanFactory beanFactory;

    private Environment environment;

    private RelaxedPropertyResolver propertyResolver;

    /**
     * 客户端代理对象池
     */
    private Map<Class<?>, Object> clientObjects = new ConcurrentHashMap<Class<?>, Object>();

    /**
     * 抽象服务客户端代理对象池，服务编号＋服务组->客户端对象
     */
    private Map<String, Object> abstractServiceClientObjects = new ConcurrentHashMap<String, Object>();

    private ExecutorService asyncCallResultHandlerExecutorService;

    private Object newClientObject(ServiceClass srvClass) {
        XproRemoteInvocationHandler invHandler = new XproRemoteInvocationHandler();
        invHandler.setServiceClass(srvClass);
        invHandler.setProtocolFactorySelector(protocolFactorySelector);
        invHandler.setServerObjectRegistry(serverObjectRegistry);
        invHandler.setAsyncCallResultHandlerExecutorService(asyncCallResultHandlerExecutorService);
        invHandler.setRestTemplate(restTemplate);
        invHandler.setLdRestTemplate(ldRestTemplate);
        invHandler.setXproProperties(xproProperties);

        DiscoveryClient discoveryClient = beanFactory.getBean(DiscoveryClient.class);
        invHandler.setDiscoveryClient(discoveryClient);

        String gatewayName = propertyResolver.getProperty("gateway.name");
        invHandler.setGatewayName(gatewayName);

        RemoteStub stub = new RemoteStub();
        stub.setServiceId(srvClass.getDescription().getServiceId());
        stub.setServiceGroup(srvClass.getDescription().getServiceGroup());
        stub.setGatewayName(srvClass.getDescription().getGatewayName());
        stub.setAppName(srvClass.getDescription().getAppName());
        stub.setContentType(srvClass.getDescription().getCallbackContentType());
        stub.setCallbackObject(srvClass.getDescription().isCallbackObject());
        String addr = srvClass.getDescription().getCallbackAddress();
        stub.setServerAddress(addr);

        invHandler.setRemoteStub(stub);

        Class<?> srvInfClazz = srvClass.getInfClazz();
        ClassLoader cl = srvInfClazz.getClassLoader();
        Object clientObj = Proxy.newProxyInstance(cl, new Class[] {srvInfClazz, RemoteObject.class}, invHandler);

        return clientObj;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        propertyResolver = new RelaxedPropertyResolver(environment);
        xproProperties = beanFactory.getBean(XproProperties.class);
        asyncCallResultHandlerExecutorService = Executors.newFixedThreadPool(xproProperties.getAsyncCallResultHandlerConcurrent());
    }

    @Override
    public <T> T getClientObject(Class<T> serviceInterface) {
        T clientObj = (T) clientObjects.get(serviceInterface);
        if (clientObj != null) {
            return clientObj;
        }

        ServiceClass srvClass = ServiceClass.newServiceClass(serviceInterface, null);
        clientObj = (T) newClientObject(srvClass);
        clientObjects.put(serviceInterface, clientObj);

        return clientObj;
    }

    @Override
    public <T> T getClientObjectForCallback(String gatewayName, String appName, String serviceId, String serviceGroup, String callbackAddr, String contentType, Class<T> inf, boolean callbackObj) {
        ServiceClass srvClass = ServiceClass.newCallbackClass(inf, gatewayName, appName, serviceId, serviceGroup, callbackAddr, contentType, callbackObj);
        return (T) newClientObject(srvClass);
    }

    @Override
    public <T> T getClientObject(String stubSerializeData, Class<T> infClazz) {
        RemoteStub stub = new RemoteStub();
        stub.deserializeStub(stubSerializeData);
        return getClientObjectForCallback(stub.getGatewayName(), stub.getAppName(), stub.getServiceId(), stub.getServiceGroup(), stub.getServerAddress(), stub.getContentType(), infClazz, stub.isCallbackObject());
    }

    @Override
    public void init() {
        if (rpcServiceCallbackProxy == null) {
            DefaultRpcServiceCallbackProxy defRpcServiceCallbackProxy = new DefaultRpcServiceCallbackProxy();
            this.rpcServiceCallbackProxy = defRpcServiceCallbackProxy;

            defRpcServiceCallbackProxy.setClientObjectFactory(this);
        }

        ProtectedRpcClientContextAccessor.setCallbackProxy(rpcServiceCallbackProxy);
    }

    @Override
    public void start() {}

    @Override
    public void stop() {
        asyncCallResultHandlerExecutorService.shutdown();
    }

    @Override
    public <T> T getClientObject(Class<T> infClazz, String serviceId, String serviceGroup) {
        StringBuffer key = new StringBuffer();
        key.append(serviceId);
        key.append(",");
        key.append(serviceGroup);

        T clientObj = (T) abstractServiceClientObjects.get(key.toString());
        if (clientObj != null) {
            return clientObj;
        }

        ServiceClass srvClass = ServiceClass.newServiceClass(infClazz, serviceId, serviceGroup);
        clientObj = (T) newClientObject(srvClass);
        abstractServiceClientObjects.put(key.toString(), clientObj);
        return clientObj;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
