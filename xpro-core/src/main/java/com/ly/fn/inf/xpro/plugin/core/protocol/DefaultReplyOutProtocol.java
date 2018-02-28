package com.ly.fn.inf.xpro.plugin.core.protocol;

import com.ly.fn.inf.rpc.annotaion.Poly;
import com.ly.fn.inf.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 缺省实现的Rpc服务应答输出协议器类。
 */
public class DefaultReplyOutProtocol implements ReplyOutProtocol {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private int stage = 0;

    /**
     * 请求消息
     */
    private Message msg;

    /**
     * 字节输出流
     */
    private ByteArrayOutputStream bout = new ByteArrayOutputStream();

    /**
     * Frame结构输出流
     */
    private FrameOutputStream fout = new FrameOutputStream(bout);

    /**
     * 应答头对象
     */
    private ReplyHeader header;

    /**
     * 序列化器
     */
    private Serializer serializer;

    /**
     * 内容类型
     */
    private String contentType;

    public DefaultReplyOutProtocol(Serializer serializer, String contentType) {
        this.serializer = serializer;
        this.contentType = contentType;
    }

    private void checkStatus(int exceptStage) {
        if (stage == exceptStage) {
            return;
        }

        if (stage == 0) {
            throw new IllegalProtocolStatusException("Need set header firstly.");
        }

        if (stage == 1) {
            throw new IllegalProtocolStatusException("Header has been setted.");
        }

        if (stage == 2) {
            throw new IllegalProtocolStatusException("Body has been setted.");
        }
    }

    public void writeHeader(ReplyHeader header) {
        checkStatus(0);

        byte[] serData = serializer.serialize(header);
        fout.writeFrameBytes(serData);
        this.header = header;
        stage = 1;
    }

    private byte[] serialize(Type type, Object obj) {
        try {
            return serializer.serialize(type, obj);
        } catch (RuntimeException e) {
            JacksonSerializer ser = JacksonSerializer.newPrettySerializer();
            logger.error("Serialize meet error, bean=[" + new String(ser.serialize(obj)) + "].");
            throw e;
        }
    }

    public void writeRetObject(Method method, Type retType, Object retObj) {
        checkStatus(1);

        if (!header.getType().equals(ReplyHeader.TYPE_RETURN)) {
            throw new IllegalProtocolStatusException("Body can't been retObject.");
        }

        byte[] serData;
        if (method.isAnnotationPresent(Poly.class)) {
            ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
            FrameOutputStream fout2 = new FrameOutputStream(bout2);
            fout2.writeFrameBytes(retObj.getClass().getName().getBytes());
            fout2.writeFrameBytes(serialize(retObj.getClass(), retObj));
            serData = bout2.toByteArray();
        } else {
            serData = serialize(retType, retObj);
        }

        fout.writeFrameBytes(serData);

        stage = 2;
    }

    public void writeException(Throwable e) {
        checkStatus(1);

        if (!header.getType().equals(ReplyHeader.TYPE_EXCEPTION)) {
            throw new IllegalProtocolStatusException("Body can't been exception.");
        }

        // Throwable存在非泛化类型，因此使用Java本地序列化方法
        byte[] serData;

        try {
            serData = JavaNativeSerializer.serialize(e);
        } catch (Exception e1) {
            // JavaNative序列化出现问题，简化异常对象
            RuntimeException ex = new RuntimeException(e.getMessage());
            ex.setStackTrace(e.getStackTrace());
            ex.initCause(e.getCause());
            serData = JavaNativeSerializer.serialize(ex);
        }

        serData = ZipUtil.zipBytes(serData);
        fout.writeFrameBytes(serData);

        // 同时写入异常基本信息信息，用于客户端无法反序列化时候使用
        ExceptionData exData = new ExceptionData(e);
        serData = serialize(ExceptionData.class, exData);
        fout.writeFrameBytes(serData);

        stage = 2;
    }

    public Message getMessage() {
        if (stage != 1) {
            checkStatus(2);
        }

        if (msg != null) {
            return msg;
        }

        msg = new Message();
        msg.setContentType(contentType);
        msg.setData(bout.toByteArray());
        return msg;
    }

    public void reset() {
        stage = 0;
        bout = new ByteArrayOutputStream();
        fout = new FrameOutputStream(bout);
    }

}
