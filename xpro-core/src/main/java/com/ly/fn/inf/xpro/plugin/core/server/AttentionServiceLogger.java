package com.ly.fn.inf.xpro.plugin.core.server;

import com.ly.fn.inf.util.JacksonSerializer;
import com.ly.fn.inf.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 需要注意的服务日志器类，主要负责记录Sla达不到规定的服务调用。
 */
public class AttentionServiceLogger {
    private static Logger logger = LoggerFactory.getLogger("AttentionServiceLogger");

    /**
     * 服务超载
     */
    public static final String TYPE_SERVICE_OVERLOAD = "service-overload";

    /**
     * 服务错误（运行级别异常）
     */
    public static final String TYPE_SERVICE_ERROR = "service-error";

    /**
     * 序列化器
     */
    private static Serializer serializer = new JacksonSerializer(false);

    private static void log(String type, Object item) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(type);
            sb.append(":");
            String log = new String(serializer.serialize(item));
            sb.append(log);
            logger.error(sb.toString());
        } catch (Throwable e) {
            logger.error("AttentionServiceLogger meet error.", e);
        }
    }

    /**
     * 记录关注服务的过载信息。
     */
    public static void log(AttentionServiceOverloadLogItem item) {
        log(TYPE_SERVICE_OVERLOAD, item);
    }
}
