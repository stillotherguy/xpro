package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.xpro.plugin.core.NotFoundServiceException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mitsui
 * @since 2018年01月10日
 */
public class DefaultServiceObjectHolder implements ServiceObjectBinder, ServiceObjectFinder {

    /**
     * 服务对象集
     */
    private Map<String, ServiceObject> serviceObjects = new ConcurrentHashMap<String, ServiceObject>();

    @Override
    public void bind(ServiceObject srvObj) {
        serviceObjects.put(srvObj.getServiceId(), srvObj);
    }

    @Override
    public boolean unbind(String serviceId) {
        return serviceObjects.remove(serviceId) != null;
    }

    @Override
    public ServiceObject getServiceObject(String serviceId, String method) throws NotFoundServiceException {
        ServiceObject srvObj = serviceObjects.get(serviceId);
        if (srvObj == null) {
            throw new NotFoundServiceException(serviceId, method);
        }

        return srvObj;
    }
}
