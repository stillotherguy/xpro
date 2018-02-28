package com.ly.fn.inf.xpro.plugin.core.protocol;

import com.ly.fn.inf.util.JacksonSerializer;
import com.ly.fn.inf.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 缺省协议工厂类。
 */
public class DefaultProtocolFactory implements ProtocolFactory {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 缺省序列化器
     */
    private Serializer defaultSerializer;

    /**
     * 缺省内容类型
     */
    private String defaultContentType;

    /**
     * 对象序列化器集，cotentType->serializer
     */
    private Map<String, Serializer> serializers = new HashMap<String, Serializer>();

    /**
     * 内容类型
     */
    private String contentType;

    public DefaultProtocolFactory() {
        this(MessageContentTypes.JSON_JACKSON_SMILE);
    }

    public DefaultProtocolFactory(String contentType) {
        if (contentType.equals(MessageContentTypes.JSON_JACKSON)) {
            Serializer serializer = new JacksonSerializer(false);
            serializers.put(contentType, serializer);

            logger.info("Init serializer for contentType=" + contentType + ".");
        } else if (contentType.equals(MessageContentTypes.JSON_JACKSON_SMILE)) {
            Serializer serializer = new JacksonSerializer(true);
            serializers.put(contentType, serializer);

            logger.info("Init serializer for contentType=" + contentType + ".");
        } else {
            throw new RuntimeException("Unsupported contentType=[" + contentType + "].");
        }

        this.contentType = contentType;
        this.defaultContentType = contentType;

        this.defaultSerializer = serializers.get(defaultContentType);
    }

    public String getDefaultContentType() {
        return null;
    }

    private Serializer getSerializer(String contentType) {
        Serializer serializer = serializers.get(contentType);
        if (serializer == null) {
            throw new RuntimeException("Not found the serializer for content-type=[" + contentType + "].");
        }

        return serializer;
    }

    /**
     * 生成RequestInProtocol
     */
    public RequestInProtocol newRequestInProtocol(Message msg) {
        return new DefaultRequestInProtocol(getSerializer(msg.getContentType()), msg);
    }

    /**
     * 生成RequestOutProtocol
     */
    public RequestOutProtocol newRequestOutProtocol() {
        return new DefaultRequestOutProtocol(defaultSerializer, defaultContentType);
    }

    /**
     * 生成ReplyInProtocol
     */
    public ReplyInProtocol newReplyInProtocol(Message msg) {
        return new DefaultReplyInProtocol(getSerializer(msg.getContentType()), msg);
    }

    /**
     * 生成ReplyOutProtocol
     */
    public ReplyOutProtocol newReplyOutProtocol(String contentType) {
        return new DefaultReplyOutProtocol(getSerializer(contentType), contentType);
    }

    public String supportContentType() {
        return contentType;
    }
}
