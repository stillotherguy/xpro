package com.ly.fn.inf.xpro.plugin.core.protocol;

import com.ly.fn.inf.rpc.annotaion.Callback;
import com.ly.fn.inf.rpc.annotaion.JavaNativeSerialize;
import com.ly.fn.inf.rpc.annotaion.Poly;
import com.ly.fn.inf.util.FrameOutputStream;
import com.ly.fn.inf.util.JavaNativeSerializer;
import com.ly.fn.inf.util.Serializer;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 缺省实现的Rpc服务请求协议器类。
 */
public class DefaultRequestOutProtocol implements RequestOutProtocol {
    private int stage = 0;

    /**
     * 请求消息
     */
    private Message msg;

    /**
     * 消息属性集
     */
    private Map<String, String> messageProps;

    /**
     * 字节输出流
     */
    private ByteArrayOutputStream bout = new ByteArrayOutputStream();

    /**
     * Frame结构输出流
     */
    private FrameOutputStream fout = new FrameOutputStream(bout);

    /**
     * 请求头对象
     */
    private RequestHeader header;

    /**
     * 序列化器
     */
    private Serializer serializer;

    /**
     * 内容类型
     */
    private String contentType;

    public DefaultRequestOutProtocol(Serializer serializer, String contentType) {
        this.serializer = serializer;
        this.contentType = contentType;
    }

    public void writeHeader(RequestHeader header) {
        if (stage != 0) {
            throw new IllegalProtocolStatusException("Header has been setted.");
        }

        String srvId = header.getServiceId();
        String srvGrp = header.getServiceGroup();
        String method = header.getMethod();

        messageProps = header.getProps();
        byte[] serData = serializer.serialize(header);

        header.setServiceGroup(srvGrp);
        header.setServiceId(srvId);
        header.setMethod(method);

        fout.writeFrameBytes(serData);
        this.header = header;
        stage = 1;
    }

    public void writeArgs(Method method, Object[] args) {
        if (stage == 0) {
            throw new IllegalProtocolStatusException("Need set the header firstly.");
        }

        if (stage == 2) {
            throw new IllegalProtocolStatusException("Args has been setted.");
        }

        if (args == null) {
            if (header.getArgsCount() != 0) {
                throw new IllegalProtocolStatusException("Mismatched args count, args.count != header.argsCount.");
            }

            stage = 2;
            return;
        }

        if (args.length != header.getArgsCount()) {
            throw new IllegalProtocolStatusException("Mismatched args count, args.count=[" + args.length + "] != header.argsCount=[" + header.getArgsCount() + "].");
        }

        Annotation[][] paraAnnos = method.getParameterAnnotations();
        Type[] argTypes = method.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            Annotation[] annos = paraAnnos[i];

            Callback callback = null;
            boolean nativeSerFlag = false;
            boolean polyArgFlag = false;
            for (Annotation anno : annos) {
                if (anno instanceof Callback) {
                    callback = (Callback) anno;
                } else if (anno instanceof JavaNativeSerialize) {
                    nativeSerFlag = true;
                } else if (anno instanceof Poly) {
                    polyArgFlag = true;
                }
            }

            byte[] serData;
            if (nativeSerFlag) {
                serData = JavaNativeSerializer.serialize(args[i]);
            } else {
                if (callback != null) {
                    serData = serializer.serialize(CallbackArg.class, args[i]);
                } else {
                    if (polyArgFlag) {
                        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
                        FrameOutputStream fout2 = new FrameOutputStream(bout2);
                        fout2.writeFrameBytes(args[i].getClass().getName().getBytes());
                        fout2.writeFrameBytes(serializer.serialize(args[i].getClass(), args[i]));
                        serData = bout2.toByteArray();
                    } else {
                        serData = serializer.serialize(argTypes[i], args[i]);
                    }
                }
            }

            fout.writeFrameBytes(serData);
        }

        stage = 2;
    }

    public Message getMessage() {
        if (stage == 0) {
            throw new IllegalProtocolStatusException("Need set the header at least.");
        }

        if (stage == 1) {
            if (header.getArgsCount() != 0) {
                throw new IllegalProtocolStatusException("Need set the args.");
            }
        }

        if (msg != null) {
            return msg;
        }

        msg = new Message();
        msg.setContentType(contentType);
        msg.setProperties(messageProps);
        msg.setData(bout.toByteArray());

        return msg;
    }

}
