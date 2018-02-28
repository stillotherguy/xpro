package com.ly.fn.inf.xpro.plugin.core.protocol;

import com.ly.fn.inf.util.CheckHashMap;
import com.ly.fn.inf.util.ConcurrentCheckHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 缺省协议工厂选择器实现类。
 */
public class DefaultProtocolFactorySelector implements ProtocolFactorySelector {
    /**
     * 内容类型->协议工厂映射表
     */
    private Map<String, ProtocolFactory> protocolFactoryByContentType = new ConcurrentCheckHashMap<String, ProtocolFactory>(UnsupportedContentTypeException.class);

    /**
     * 协议工厂集
     */
    private List<ProtocolFactory> protocolFactories = new ArrayList<ProtocolFactory>();

    public ProtocolFactory select(String contentType) {
        int idx = contentType.lastIndexOf(":");
        if (idx > 0) {
            contentType = contentType.substring(0, idx);
        }

        return protocolFactoryByContentType.get(contentType);
    }

    public void addProtocolFactory(ProtocolFactory factory) {
        this.protocolFactories.add(factory);
        this.protocolFactoryByContentType.put(factory.supportContentType(), factory);
    }

    public List<ProtocolFactory> getProtocolFactories() {
        return protocolFactories;
    }

    public void setProtocolFactories(List<ProtocolFactory> protocolFactories) {
        this.protocolFactories = protocolFactories;
        Map<String, ProtocolFactory> m = new CheckHashMap<String, ProtocolFactory>(UnsupportedContentTypeException.class);
        for (ProtocolFactory factory : protocolFactories) {
            m.put(factory.supportContentType(), factory);
        }
        this.protocolFactoryByContentType = m;
    }
}
