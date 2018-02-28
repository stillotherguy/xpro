package com.ly.fn.inf.xpro.plugin.core.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Transport层消息定义类。
 */
public class Message {
    /**
     * 属性
     */
    private Map<String, String> properties;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 消息建立时间
     */
    private long createTime;

    /**
     * 数据
     */
    private byte[] data;

    public String getProperty(String name) {
        if (properties == null) {
            return null;
        }

        return properties.get(name);
    }

    public void setProperty(String name, String value) {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }

        properties.put(name, value);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
