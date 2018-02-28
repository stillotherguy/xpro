package com.ly.fn.inf.xpro.plugin.core.protocol;

import com.ly.fn.inf.rpc.annotaion.Callback;
import com.ly.fn.inf.rpc.annotaion.JavaNativeSerialize;
import com.ly.fn.inf.rpc.annotaion.Poly;
import com.ly.fn.inf.util.FrameInputStream;
import com.ly.fn.inf.util.JavaNativeSerializer;
import com.ly.fn.inf.util.Serializer;
import com.ly.fn.inf.xpro.plugin.api.PropertyNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 缺省实现的Rpc服务请求输入协议器类。
 */
public class DefaultRequestInProtocol implements RequestInProtocol {
    public static final Logger LOG = LoggerFactory.getLogger(DefaultRequestInProtocol.class);
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
    private RequestHeader header;

    /**
     * 参数对象
     */
    private Object[] args;

    /**
     * 序列化器
     */
    private Serializer serializer;

    public DefaultRequestInProtocol(Serializer serializer, Message msg) {
        this.msg = msg;
        this.serializer = serializer;

        ByteArrayInputStream bin = new ByteArrayInputStream(msg.getData());
        fin = new FrameInputStream(bin);

        byte[] data = fin.readFrameBytes();

        header = serializer.deserialize(RequestHeader.class, data);

        String crossDMZFlag = msg.getProperty(PropertyNames.CROSS_DMS_FLAG);
        if (crossDMZFlag != null) {
            header.setCrossDMZFlag(Boolean.valueOf(crossDMZFlag));
        }
    }

    public RequestHeader readHeader() {
        return header;
    }

    public Object[] readArgs(Method method) {
        if (args != null) {
            return args;
        }

        args = new Object[method.getParameterTypes().length];
        int argsCount = header.getArgsCount();
        if (argsCount > args.length) {
            // 参数匹配就小原则
            argsCount = args.length;
        }

        Type[] genericParaTypes = method.getGenericParameterTypes();
        Annotation[][] paraAnnos = method.getParameterAnnotations();
        for (int i = 0; i < args.length; i++) {
            if (i < argsCount) {
                byte[] data = fin.readFrameBytes();
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

                if (callback != null) {
                    args[i] = serializer.deserialize(CallbackArg.class, data);
                } else {
                    if (nativeSerFlag) {
                        args[i] = JavaNativeSerializer.deserialize(data);
                    } else {
                        Type type = genericParaTypes[i];
                        if (polyArgFlag) {
                            FrameInputStream fin2 = new FrameInputStream(new ByteArrayInputStream(data));
                            String polyArgClassName = new String(fin2.readFrameBytes());
                            data = fin2.readFrameBytes();
                            try {
                                type = Class.forName(polyArgClassName);
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        }

                        args[i] = serializer.deserialize(type, data);
                    }
                }
            } else {
                args[i] = null;
            }
        }

        return args;
    }

    public Map<String, String> getMessageProperties() {
        return msg.getProperties();
    }

    public Message getMessage() {
        return msg;
    }

}
