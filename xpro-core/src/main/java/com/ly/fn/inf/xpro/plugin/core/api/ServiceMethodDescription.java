package com.ly.fn.inf.xpro.plugin.core.api;

import com.ly.fn.inf.rpc.annotaion.Sla;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务方法描述类。
 */
public class ServiceMethodDescription {
    /**
     * 异步标志
     */
    private boolean asynchronous;

    /**
     * Sla参数，超时时间（毫秒）
     */
    private long timeout;

    /**
     * Sla参数，平均处理时长（毫秒）
     */
    private long avgTime = Sla.DEFAULT_AVG_TIME;

    /**
     * Sla参数，优先级别
     */
    private int priority;

    /**
     * 请求范围缓存标志
     */
    private boolean requestScopeCache;

    public static ServiceMethodParameterDescription NONE_DESCRIPTION = new ServiceMethodParameterDescription();

    /**
     * 参数描述
     */
    private List<ServiceMethodParameterDescription> paraDescs = new ArrayList<ServiceMethodParameterDescription>();

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ServiceMethodParameterDescription getParameterDescription(int argIdx) {
        if (paraDescs == null) {
            return NONE_DESCRIPTION;
        }

        return paraDescs.get(argIdx);
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    public List<ServiceMethodParameterDescription> getParaDescs() {
        return paraDescs;
    }

    public void setParaDescs(List<ServiceMethodParameterDescription> paraDescs) {
        this.paraDescs = paraDescs;
    }

    public boolean isRequestScopeCache() {
        return requestScopeCache;
    }

    public void setRequestScopeCache(boolean requestScopeCache) {
        this.requestScopeCache = requestScopeCache;
    }

    public long getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(long avgTime) {
        this.avgTime = avgTime;
    }
}
