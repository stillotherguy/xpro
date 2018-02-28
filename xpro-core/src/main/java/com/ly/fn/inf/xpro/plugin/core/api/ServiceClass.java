package com.ly.fn.inf.xpro.plugin.core.api;

import com.ly.fn.inf.rpc.annotaion.RpcAbstractService;
import com.ly.fn.inf.rpc.annotaion.RpcService;
import com.ly.fn.inf.rpc.annotaion.Self;
import com.ly.fn.inf.rpc.annotaion.Sla;
import com.ly.fn.inf.util.ContainerValueHashMap;
import com.ly.fn.inf.util.StringUtil;
import com.ly.fn.inf.xpro.plugin.api.ConfigurableRpcService;
import com.ly.fn.inf.xpro.plugin.api.annotation.XproAppConfig;
import com.ly.fn.inf.xpro.plugin.core.server.AppInfo;
import com.ly.fn.inf.xpro.plugin.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rpc服务类描述类。
 */
@Slf4j
public class ServiceClass {
    public static final String CALLBACK_OBJECT_SERVICE_ID_PREFIX = "__cb.";

    /**
     * 缓存的服务类
     */
    private static Map<String, ServiceClass> cachedServiceClass = new ConcurrentHashMap<String, ServiceClass>();

    /**
     * Rpc服务接口类对象。
     */
    private Class<?> infClazz;

    /**
     * 服务描述
     */
    private ServiceDescription serviceDescription;

    /**
     * 方法映射表
     */
    private Map<String, Map<String, ServiceMethod>> methods = new ContainerValueHashMap<String, Map<String, ServiceMethod>>(HashMap.class);

    /**
     * 方法映射表（方法通用名称作为Key）
     */
    private Map<String, ServiceMethod> methodByHashCode = new HashMap<String, ServiceMethod>();

    private static ServiceClass getCachedServiceClass(Class<?> infClass, boolean cloneFlag) {
        ServiceClass srvClass = cachedServiceClass.get(infClass.getName());
        if (srvClass == null) {
            return null;
        }

        if (cloneFlag == false) {
            return srvClass;
        }

        // 复制非服务层描述－方法描述部分
        ServiceClass cloneSrvClass = new ServiceClass();
        cloneSrvClass.infClazz = srvClass.infClazz;
        cloneSrvClass.methodByHashCode = srvClass.methodByHashCode;
        cloneSrvClass.methods = srvClass.methods;

        // 复制服务固定属性部分
        cloneSrvClass.serviceDescription = new ServiceDescription();
        cloneSrvClass.serviceDescription.setAvgTime(srvClass.serviceDescription.getAvgTime());
        cloneSrvClass.serviceDescription.setPriority(srvClass.serviceDescription.getPriority());
        cloneSrvClass.serviceDescription.setTimeout(srvClass.serviceDescription.getTimeout());
        cloneSrvClass.serviceDescription.setServiceGroup(srvClass.serviceDescription.getServiceGroup());
        cloneSrvClass.serviceDescription.setServiceId(srvClass.serviceDescription.getServiceId());
        cloneSrvClass.serviceDescription.setGatewayName(srvClass.serviceDescription.getGatewayName());
        cloneSrvClass.serviceDescription.setAppName(srvClass.serviceDescription.getAppName());

        return cloneSrvClass;
    }

    private static void cacheServiceClass(Class<?> infClass, ServiceClass srvClass) {
        cachedServiceClass.put(infClass.getName(), srvClass);
    }

    private static void fillServiceDescription(ServiceDescription srvDesc, Class<?> clazz) {
        Sla srvSla = clazz.getAnnotation(Sla.class);
        if (srvSla != null) {
            srvDesc.setTimeout(srvSla.timeout());
            srvDesc.setPriority(srvSla.priority());
            srvDesc.setAvgTime(srvSla.avgTime());
        }
    }

    private static void checkAbstractService(Class<?> infClazz) {
        if (infClazz.getAnnotation(RpcAbstractService.class) == null) {
            throw new RuntimeException("ServiceInterface=[" + infClazz.getName() + "] hasn't RpcAbstractService com.ly.fn.inf.xpro.plugin.api.annotation.");
        }
    }

    public static boolean isCallbackObject(String serviceId) {
        return serviceId.startsWith(CALLBACK_OBJECT_SERVICE_ID_PREFIX);
    }

    public static ServiceDescription getServiceDescription(Class<?> clazz, String serviceId, String serviceGroup, String gatewayName, String appName) {
        RpcService RpcSrvAnno = clazz.getAnnotation(RpcService.class);
        Class<?> infClazz = null;
        ServiceDescription srvDesc = new ServiceDescription();
        if (RpcSrvAnno == null) {
            // 由于是匿名内部类抽象服务，所以appName，gatewayName，serviceGroup从callbackArg拿到
            if (serviceId == null && serviceGroup == null) {
                throw new MissingAnnotationException("The class must have RpcService com.ly.fn.inf.xpro.plugin.api.annotation, class=" + clazz);
            }

            RpcAbstractService RpcAbstractSrvAnno = clazz.getAnnotation(RpcAbstractService.class);
            if (RpcAbstractSrvAnno == null) {
                throw new MissingAnnotationException("The class must have RpcAbstractService com.ly.fn.inf.xpro.plugin.api.annotation, class=" + clazz);
            }

            if (serviceId == null) {
                // class name as default serviceId.
                serviceId = clazz.getName();
            }

            //srvDesc.setGatewayName(gatewayName);
            //srvDesc.setAppName(appName);
        } else {
            if (RpcSrvAnno.serviceInterface().equals(Self.class) == false) {
                // other interface define
                infClazz = RpcSrvAnno.serviceInterface();
                checkAbstractService(infClazz);

                if (ReflectUtil.isAncestor(RpcSrvAnno.serviceInterface(), clazz) == false) {
                    throw new RuntimeException("The serviceInterface=[" + RpcSrvAnno.serviceInterface().getName() + " isn't anestor of the class=[" + clazz.getName() + "].");
                }
            }

            if (serviceId == null) {
                serviceId = RpcSrvAnno.serviceId();
                if (serviceId.equals("")) {
                    // class name as default serviceId.
                    serviceId = clazz.getName();
                }
            }

            if (serviceGroup == null) {
                serviceGroup = StringUtil.emptyAsNull(RpcSrvAnno.serviceGroup());
            }

            if (appName == null) {
                Package pkg = clazz.getPackage();
                XproAppConfig xproAppConfig = null;
                if (!pkg.isAnnotationPresent(XproAppConfig.class) || (xproAppConfig = pkg.getAnnotation(XproAppConfig.class)) == null) {
                    // 如果是抽象服务实现
                    if (!clazz.isInterface()) {
                        AppInfo appInfo = AppInfo.getInstance();
                        srvDesc.setGatewayName(appInfo.getGatewayName());
                        srvDesc.setAppName(appInfo.getAppName());
                    } else {
                        log.error("serviceInterface : {} Package must be Annotationed by @XproAppConfig", clazz);

                        throw new RuntimeException(clazz.getName() + " Package must be Annotationed by @XproAppConfig");
                    }
                } else {
                    srvDesc.setGatewayName(xproAppConfig.gatewayName());
                    srvDesc.setAppName(xproAppConfig.value());
                }
            } else {
                srvDesc.setGatewayName(gatewayName);
                srvDesc.setAppName(appName);
            }
        }

        srvDesc.setServiceId(serviceId);
        srvDesc.setServiceGroup(serviceGroup);

        if (infClazz != null) {
            fillServiceDescription(srvDesc, infClazz);
            return srvDesc;
        }

        fillServiceDescription(srvDesc, clazz);

        return srvDesc;
    }

    /**
     * 获取远程回调对象服务描述
     * @param clazz 可能为rpc服务实现类
     * @param targetObject 可能为null
     * @return
     */
    public static ServiceDescription getServiceDescription(Class<?> clazz, Object targetObject) {
        String gatewayName = null;
        String appName = null;
        String serviceId = null;
        String serviceGroup = null;
        if (targetObject instanceof ConfigurableRpcService) {
            ConfigurableRpcService cfgRpcService = (ConfigurableRpcService) targetObject;
            serviceId = cfgRpcService.getServiceId();
            serviceGroup = cfgRpcService.getServiceGroup();

            AppInfo appInfo = AppInfo.getInstance();
            gatewayName = appInfo.getGatewayName();
            appName = appInfo.getAppName();

            if (serviceId == null && serviceGroup == null) {
                throw new RuntimeException("serviceId or serviceGroup is required in ConfigurableRpcService=[" + targetObject.getClass().getName() + "].");
            }
        }

        return getServiceDescription(clazz, serviceId, serviceGroup, gatewayName, appName);
    }

    private void initMethodDescritpion() {
        Method[] methodList = infClazz.getMethods();

        Map<String, Set<String>> removeList = new ContainerValueHashMap<String, Set<String>>(HashSet.class);

        for (Method method : methodList) {
            ServiceMethod srvMethod = new ServiceMethod(serviceDescription, method);

            methodByHashCode.put(ServiceMethod.getGenericMethodName(method), srvMethod);
            Map<String, ServiceMethod> mapForMethod = methods.get(method.getName());

            ServiceMethod conflictMethod = mapForMethod.put(srvMethod.getArgsSignature(), srvMethod);
            if (conflictMethod != null) {
                // conflicting due same argSignature
                // add argsSignature to removeList
                Set<String> removeSet = removeList.get(method.getName());
                removeSet.add(srvMethod.getArgsSignature());

                // regen fullArgsSignature as key for conflicting methods.
                conflictMethod.regenerateArgsSignature(method);
                srvMethod.regenerateArgsSignature(conflictMethod.getMethod());
                mapForMethod.put(srvMethod.getArgsSignature(), srvMethod);
                mapForMethod.put(conflictMethod.getArgsSignature(), conflictMethod);
            }
        }

        // Remove conflicting argsSignature
        for (Entry<String, Set<String>> entry : removeList.entrySet()) {
            Map<String, ServiceMethod> mapForMethod = methods.get(entry.getKey());
            for (String argsSignature : entry.getValue()) {
                mapForMethod.remove(argsSignature);
            }
        }
    }

    private ServiceClass() {}

    private void initServiceClassServicePart(Class<?> infClazz, CallbackDescription callbackDesc) {
        serviceDescription = new ServiceDescription();
        serviceDescription.setCallbackObject(true);
        serviceDescription.setServiceId(CALLBACK_OBJECT_SERVICE_ID_PREFIX + UUID.randomUUID().toString());
        serviceDescription.setCallbackLifecycle(callbackDesc.getLifecycle());
        serviceDescription.setCallbackTtl(callbackDesc.getTtl());
    }

    private void initServiceClass(Class<?> infClazz, CallbackDescription callbackDesc) {
        this.infClazz = infClazz;
        checkAbstractService(this.infClazz);

        initServiceClassServicePart(infClazz, callbackDesc);
        initMethodDescritpion();
    }

    /**
     * 匿名内部类回调
     * @param infClazz
     * @param callbackDesc
     * @return
     */
    public static ServiceClass newCallbackClass(Class<?> infClazz, CallbackDescription callbackDesc) {
        ServiceClass srvClass = getCachedServiceClass(infClazz, true);
        if (srvClass != null) {
            srvClass.initServiceClassServicePart(infClazz, callbackDesc);
        } else {
            srvClass = new ServiceClass();
            srvClass.initServiceClass(infClazz, callbackDesc);
            cacheServiceClass(infClazz, srvClass);
        }

        return srvClass;
    }

    private void initServiceClassServicePart(Class<?> callbackInf, String callbackGatewayName, String callbackAppName, String callbackSrvId, String callbackSrvGrp, String callbackAddr, String contentType, boolean callbackObj) {
        serviceDescription = new ServiceDescription();
        serviceDescription.setGatewayName(callbackGatewayName);
        serviceDescription.setAppName(callbackAppName);
        serviceDescription.setCallbackObject(callbackObj);
        serviceDescription.setServiceId(callbackSrvId);
        serviceDescription.setServiceGroup(callbackSrvGrp);
        serviceDescription.setCallbackAddress(callbackAddr);
        serviceDescription.setCallbackContentType(contentType);

        fillServiceDescription(serviceDescription, infClazz);
    }

    private void initServiceClass(Class<?> callbackInf, String callbackGatewayName, String callbackAppName, String callbackSrvId, String callbackSrvGrp, String callbackAddr, String contentType, boolean callbackObj) {
        this.infClazz = callbackInf;
        checkAbstractService(this.infClazz);

        initServiceClassServicePart(callbackInf, callbackGatewayName, callbackAppName, callbackSrvId, callbackSrvGrp, callbackAddr, contentType, callbackObj);
        initMethodDescritpion();
    }

    public static ServiceClass newCallbackClass(Class<?> callbackInf, String callbackGatewayName, String callbackAppName, String callbackSrvId, String callbackSrvGrp, String callbackAddr, String contentType, boolean callbackObj) {
        ServiceClass srvClass = getCachedServiceClass(callbackInf, true);
        if (srvClass != null) {
            srvClass.initServiceClassServicePart(callbackInf, callbackGatewayName, callbackAppName, callbackSrvId, callbackSrvGrp, callbackAddr, contentType, callbackObj);
        } else {
            srvClass = new ServiceClass();
            srvClass.initServiceClass(callbackInf, callbackGatewayName, callbackAppName, callbackSrvId, callbackSrvGrp, callbackAddr, contentType, callbackObj);
            cacheServiceClass(callbackInf, srvClass);
        }

        return srvClass;
    }

    private ServiceClass(Class<?> clazz, Object targetObject) {
        this.infClazz = clazz;

        serviceDescription = getServiceDescription(clazz, targetObject);
        RpcService RpcSrvAnno = clazz.getAnnotation(RpcService.class);

        if (RpcSrvAnno.serviceInterface().equals(Self.class) == false) {
            this.infClazz = RpcSrvAnno.serviceInterface();
        }

        initMethodDescritpion();
    }

    private ServiceClass(Class<?> clazz, String serviceId, String serviceGroup) {
        this.infClazz = clazz;

        serviceDescription = getServiceDescription(clazz, serviceId, serviceGroup, null, null);
        initMethodDescritpion();
    }

    public static ServiceClass newServiceClass(Class<?> clazz, Object targetObject) {
        ServiceClass srvClass = getCachedServiceClass(clazz, false);
        if (srvClass != null) {
            return srvClass;
        } else {
            srvClass = new ServiceClass(clazz, targetObject);
            cacheServiceClass(clazz, srvClass);
        }

        return srvClass;
    }

    public static ServiceClass newServiceClass(Class<?> clazz, String serviceId, String serviceGroup) {
        ServiceClass srvClass = getCachedServiceClass(clazz, true);
        if (srvClass != null) {
            // 覆盖修改服务编号和服务组
            srvClass.serviceDescription.setServiceId(serviceId);
            srvClass.serviceDescription.setServiceGroup(serviceGroup);

            return srvClass;
        }

        srvClass = new ServiceClass(clazz, serviceId, serviceGroup);
        cacheServiceClass(clazz, srvClass);

        return srvClass;
    }

    public static ServiceClass newServiceClass(Class<?> clazz) {
        return newServiceClass(clazz, null);
    }

    public ServiceMethod[] getMethods() {
        return methodByHashCode.values().toArray(new ServiceMethod[0]);
    }

    public ServiceMethod getMethod(Method method) {
        return methodByHashCode.get(ServiceMethod.getGenericMethodName(method));
    }

    public ServiceMethod getMethod(String methodName, int argsCount, String argsSignature) throws NoSuchMethodException {
        Map<String, ServiceMethod> mapForMethod = methods.get(methodName);
        if (mapForMethod == null) {
            throw new NoSuchMethodException("Not found the method=[" + methodName + "].");
        }

        ServiceMethod method = null;

        if (argsSignature != null) {
            argsSignature = argsSignature.replace(" ", "");
            method = mapForMethod.get(argsSignature);
        } else {
            for (ServiceMethod m : mapForMethod.values()) {
                if (m.getArgsCount() == argsCount) {
                    if (method == null) {
                        method = m;
                    } else {
                        throw new AmbiguousMethodException("Ambiguous method=[" + methodName + "], argsCount=[" + argsCount + "].");
                    }
                }
            }
        }

        if (method == null) {
            throw new NoSuchMethodException("Not found the method=[" + methodName + "], args=[" + argsCount + "], argsSign=[" + argsSignature + "].");
        }

        return method;
    }

    public ServiceDescription getDescription() {
        return serviceDescription;
    }

    public Class<?> getInfClazz() {
        return infClazz;
    }

}
