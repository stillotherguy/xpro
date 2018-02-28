package com.ly.fn.inf.xpro.plugin.core.client;

import com.ly.fn.inf.xpro.plugin.api.RemoteObjectFactory;
import com.ly.fn.inf.xpro.plugin.core.api.ClientObjectFactory;

/**
 * 缺省远程对象工厂实现类。
 */
public class DefaultRemoteObjectFactory implements RemoteObjectFactory {
    /**
     * 客户端对象工厂
     */
    private ClientObjectFactory clientObjectFactory;

    public DefaultRemoteObjectFactory(ClientObjectFactory clientObjectFactory) {
        this.clientObjectFactory = clientObjectFactory;
    }

    public <T> T getRemoteObject(String stubSerializeData, Class<T> infClazz) {
        return clientObjectFactory.getClientObject(stubSerializeData, infClazz);
    }

    public <T> T getRemoteObject(Class<T> infClazz, String serviceId, String serviceGroup) {
        return clientObjectFactory.getClientObject(infClazz, serviceId, serviceGroup);
    }

}
