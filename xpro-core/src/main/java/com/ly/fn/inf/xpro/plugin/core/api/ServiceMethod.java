package com.ly.fn.inf.xpro.plugin.core.api;

import com.ly.fn.inf.rpc.annotaion.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Rpc服务方法描述类。
 */
public class ServiceMethod {
    /**
     * 方法对象
     */
    private Method method;

    /**
     * 参数列表关键值，如：String,Integer
     */
    private String argsSignature;

    /**
     * 参数数量
     */
    private int argsCount;

    /**
     * 服务方法描述
     */
    private ServiceMethodDescription serviceMethodDescription;

    void regenerateArgsSignature(Method conflictMethod) {
        Class<?>[] paraTypes = method.getParameterTypes();
        Class<?>[] cParaTypes = null;

        if (conflictMethod != null) {
            cParaTypes = conflictMethod.getParameterTypes();
        }

        StringBuffer sbKey = new StringBuffer();
        boolean first = true;
        int idx = 0;
        for (Class<?> paraType : paraTypes) {
            if (first == false) {
                sbKey.append(",");
            } else {
                first = false;
            }

            if (cParaTypes != null && cParaTypes[idx++] != paraType) {
                sbKey.append(paraType.getName());
            } else {
                sbKey.append(paraType.getSimpleName());
            }
        }

        argsSignature = sbKey.toString();
        argsCount = paraTypes.length;
    }

    public ServiceMethod(ServiceDescription srvDesc, Method method) {
        this.method = method;

        serviceMethodDescription = new ServiceMethodDescription();

        RpcMethod RpcMethod = method.getAnnotation(RpcMethod.class);

        if (RpcMethod != null) {
            serviceMethodDescription.setAsynchronous(RpcMethod.async());
            serviceMethodDescription.setRequestScopeCache(RpcMethod.requestScopeCache());
        }

        serviceMethodDescription.setTimeout(srvDesc.getTimeout());
        serviceMethodDescription.setPriority(srvDesc.getPriority());

        Sla sla = method.getAnnotation(Sla.class);
        if (sla != null) {
            serviceMethodDescription.setTimeout(sla.timeout());
            serviceMethodDescription.setPriority(sla.priority());
            serviceMethodDescription.setAvgTime(sla.avgTime());
        } else {
            serviceMethodDescription.setTimeout(srvDesc.getTimeout());
            serviceMethodDescription.setPriority(srvDesc.getPriority());
            serviceMethodDescription.setAvgTime(srvDesc.getAvgTime());
        }

        regenerateArgsSignature(null);

        Annotation[][] paraAnnos = method.getParameterAnnotations();
        for (int argIdx = 0; argIdx < paraAnnos.length; argIdx++) {
            List<RpcProperty> RpcPropList = new ArrayList<RpcProperty>();
            Annotation[] annos = paraAnnos[argIdx];

            Callback callback = null;
            CallbackDescription callbackDesc = null;
            RpcProperties RpcProps = null;
            RpcProperty RpcProp = null;

            for (Annotation anno : annos) {
                if (anno instanceof Callback) {
                    callback = (Callback) anno;
                } else if (anno instanceof RpcProperties) {
                    RpcProps = (RpcProperties) anno;
                } else if (anno instanceof RpcProperty) {
                    RpcProp = (RpcProperty) anno;
                }
            }

            if (callback != null) {
                callbackDesc = new CallbackDescription();
                if (callback.lifecycle() == Lifecycle.DEFAULT) {
                    if (serviceMethodDescription.isAsynchronous()) {
                        // 异步方法携带回调缺省生命周期为ONE_TIME
                        callbackDesc.setLifecycle(Lifecycle.ONE_TIME);
                    } else {
                        // 同步方法携带回调缺省生命周期为WITH_SYNC_CALL
                        callbackDesc.setLifecycle(Lifecycle.WITH_SYNC_CALL);
                    }
                } else {
                    if (serviceMethodDescription.isAsynchronous()) {
                        // async method
                        if (callback.lifecycle() == Lifecycle.WITH_SYNC_CALL) {
                            // illegal lifecycle
                            throw new RuntimeException("The callback's lifecycle can't be withSyncCall in async method=[" + method.getName() + "].");
                        }
                    }

                    callbackDesc.setLifecycle(callback.lifecycle());
                }

                callbackDesc.setTtl(callback.ttl());
            }

            if (RpcProps != null) {
                for (RpcProperty prop : RpcProps.value()) {
                    RpcPropList.add(prop);
                }
            }

            if (RpcProp != null) {
                RpcPropList.add(RpcProp);
            }

            if (RpcPropList.isEmpty() && callback == null) {
                serviceMethodDescription.getParaDescs().add(ServiceMethodDescription.NONE_DESCRIPTION);
            } else {
                ServiceMethodParameterDescription smParaDesc = new ServiceMethodParameterDescription();
                smParaDesc.setCallback(callbackDesc);
                smParaDesc.setMessageProperties(RpcPropList.isEmpty() ? null : RpcPropList);
                serviceMethodDescription.getParaDescs().add(smParaDesc);
            }
        }
    }

    public static String getGenericMethodName(Method method) {
        StringBuffer sb = new StringBuffer();
        sb.append(method.getName());
        sb.append("(");
        boolean first = true;
        for (Class<?> paraType : method.getParameterTypes()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }

            sb.append(paraType.getName());
        }

        sb.append(")");

        return sb.toString();
    }

    public Method getMethod() {
        return method;
    }

    public int getArgsCount() {
        return argsCount;
    }

    public String getArgsSignature() {
        return argsSignature;
    }

    public ServiceMethodDescription getDescription() {
        return serviceMethodDescription;
    }

}
