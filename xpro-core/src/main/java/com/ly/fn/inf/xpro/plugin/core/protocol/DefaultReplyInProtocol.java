package com.ly.fn.inf.xpro.plugin.core.protocol;

import com.ly.fn.inf.rpc.annotaion.Poly;
import com.ly.fn.inf.util.FrameInputStream;
import com.ly.fn.inf.util.JavaNativeSerializer;
import com.ly.fn.inf.util.Serializer;
import com.ly.fn.inf.util.ZipUtil;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 缺省实现的Rpc服务应答输入协议器类。
 */
public class DefaultReplyInProtocol implements ReplyInProtocol {
    /**
     * 消息对象
     */
    private Message msg;

    /**
     * Frame结构输入流
     */
    private FrameInputStream fin;

    /**
     * 头对象
     */
    private ReplyHeader header;

    /**
     * 返回对象
     */
    private Object retObject;

    /**
     * 异常对象
     */
    private Throwable exception;

    /**
     * 序列化器
     */
    private Serializer serializer;

    public DefaultReplyInProtocol(Serializer serializer, Message msg) {
        this.msg = msg;
        this.serializer = serializer;

        ByteArrayInputStream bin = new ByteArrayInputStream(msg.getData());
        fin = new FrameInputStream(bin);

        byte[] data = fin.readFrameBytes();
        header = (ReplyHeader) serializer.deserialize(ReplyHeader.class, data);

        if (header.getType().equals(ReplyHeader.TYPE_EXCEPTION)) {
            data = fin.readFrameBytes();
            data = ZipUtil.unzipBytes(data);
            try {
                exception = (Throwable) JavaNativeSerializer.deserialize(data);
            } catch (Throwable e) {
                // 无法反向序列化
                data = fin.readFrameBytes();
                ExceptionData exData = serializer.deserialize(ExceptionData.class, data);
                exception = exData.toException();
            }
        }
    }

    public ReplyHeader readHeader() {
        return header;
    }

    public Object readRetObject(Method method) {
        if (!header.getType().equals(ReplyHeader.TYPE_RETURN)) {
            throw new IllegalProtocolStatusException("Reply hasn't contained return Object.");
        }

        if (retObject != null) {
            if (retObject == VoidReturnObject.VALUE) {
                return null;
            }

            return retObject;
        }

        if (method.getGenericReturnType().equals(Void.TYPE)) {
            retObject = VoidReturnObject.VALUE;
            return null;
        }

        byte[] data = fin.readFrameBytes();
        Type type = method.getGenericReturnType();
        if (method.isAnnotationPresent(Poly.class)) {
            FrameInputStream fin2 = new FrameInputStream(new ByteArrayInputStream(data));
            String polyClassName = new String(fin2.readFrameBytes());
            data = fin2.readFrameBytes();
            try {
                type = Class.forName(polyClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        retObject = serializer.deserialize(type, data);

        return retObject;
    }

    public Throwable readException() {
        if (!header.getType().equals(ReplyHeader.TYPE_EXCEPTION)) {
            throw new IllegalProtocolStatusException("Reply hasn't contained exception.");
        }

        return exception;
    }

    public Message getMessage() {
        return msg;
    }

}
