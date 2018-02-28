package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.xpro.plugin.core.NotFoundServiceException;

/**
 * 服务对象查找器接口定义类。
 */
public interface ServiceObjectFinder {
    /**
     * 获得服务编号对应的服务对象。
     */
    ServiceObject getServiceObject(String serviceId, String method) throws NotFoundServiceException;
}
