package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.rpc.annotaion.Local;
import com.ly.fn.inf.rpc.annotaion.RpcService;
import com.ly.fn.inf.xpro.plugin.api.RemoteObjectFactory;
import com.ly.fn.inf.xpro.plugin.api.RemoteObjectFactoryAware;
import com.ly.fn.inf.xpro.plugin.core.api.CallbackDescription;
import com.ly.fn.inf.xpro.plugin.core.api.RunMode;
import com.ly.fn.inf.xpro.plugin.core.client.InternalClientObjectFactory;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ly.fn.inf.xpro.plugin.core.api.RunMode.SERVER;

/**
 * 缺省服务对象注册器实现类。
 */
public class DefaultServerObjectRegistry implements InternalServerObjectRegistry {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, ServiceObject> serviceObjects = new ConcurrentHashMap<String, ServiceObject>();

    /**
     * 客户端对象工厂对象
     */
    @Setter
    @Getter
    private InternalClientObjectFactory clientObjectFactory;

    /**
     * 远程对象工厂
     */
    @Setter
    @Getter
    private RemoteObjectFactory remoteObjectFactory;

    /**
     * 服务对象绑定
     */
    @Setter
    @Getter
    private ServiceObjectBinder serviceObjectBinder;

    /**
     * 服务对象发现
     */
    @Setter
    @Getter
    private ServiceObjectFinder serviceObjectFinder;

    /**
     * 运行模式
     */
    @Setter
    @Getter
    private RunMode runMode = SERVER;

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        serviceObjects.clear();
    }

    @Override
    public void prepareStop() {
    }

    private void registerImpl(List<ServiceObject> srvObjs, Class<?> clazz, Object targetObject, Class<?> targetClass) {
        RpcService rpcSrvAnno = clazz.getAnnotation(RpcService.class);
        if (rpcSrvAnno != null) {
            DefaultServiceObject dso = DefaultServiceObject.newServiceObject(targetObject, targetClass, clazz, clientObjectFactory, this);
            String srvId = dso.getServiceClass().getDescription().getServiceId();
            DefaultServiceObject before = (DefaultServiceObject) serviceObjects.put(srvId, dso);
            if (before != null) {
                if (before.getTargetObject() == dso.getTargetObject()) {
                    // 相同对象和相同接口服务已经注册。
                    return;
                }
                throw new RuntimeException("Duplicated serviceId=[" + srvId + "], class1=[" + targetObject.getClass().getName() + "], class2=[" + before.getClass().getName() + "].");
            }

            serviceObjectBinder.bind(dso);
            srvObjs.add(dso);
        }

        for (Class<?> infClass : clazz.getInterfaces()) {
            registerImpl(srvObjs, infClass, targetObject, targetClass);
        }

        if (clazz.getSuperclass() != null) {
            registerImpl(srvObjs, clazz.getSuperclass(), targetObject, targetClass);
        }
    }

    public boolean registerServerObject(Object targetObject, Class<?> targetClass) {
        if (targetObject instanceof RemoteObjectFactoryAware) {
            ((RemoteObjectFactoryAware) targetObject).setRemoteObjectFactory(remoteObjectFactory);
        }
        if (targetClass == null) {
            targetClass = targetObject.getClass();
        }
//        if (StringUtils.equals(targetClass.getName(), "com.ly.fn.inf.rpc.test.RpcServiceTestInvoker")
//                || StringUtils.equals(targetClass.getName(), "com.ly.fn.inf.rpc.test.RpcServiceTestInvokerImpl")) {
//            return true;
//        }
        if (targetClass.getAnnotation(Local.class) != null) {
            return false;
        }

        List<ServiceObject> srvObjs = new ArrayList<ServiceObject>();
        registerImpl(srvObjs, targetClass, targetObject, targetClass);
        if (!srvObjs.isEmpty()) {
            return true;
        }

        return false;
    }

    public boolean registerServerObject(Object targetObject) {
        return registerServerObject(targetObject, null);
    }

    public String registerCallbackObject(Object targetObject, Class<?> infClazz, CallbackDescription callbackDesc) {
        DefaultServiceObject dso = DefaultServiceObject.newCallbackObject(targetObject, infClazz, callbackDesc, clientObjectFactory, this);
        serviceObjectBinder.bind(dso);

        if (logger.isDebugEnabled()) {
            logger.debug("Register callbackObject for=[" + dso.getServiceId() + "].");
        }

        return dso.getServiceId();
    }

    @Override
    public boolean unregisterCallbackObject(String serviceId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Unregister callbackObject for=[" + serviceId + "].");
        }

        return serviceObjectBinder.unbind(serviceId);
    }
}
