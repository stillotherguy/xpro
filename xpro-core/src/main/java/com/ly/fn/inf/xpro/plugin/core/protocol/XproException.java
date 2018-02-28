package com.ly.fn.inf.xpro.plugin.core.protocol;

import com.ly.fn.inf.base.AppBizException;
import com.ly.fn.inf.base.AppRtException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0.0
 * @since 2017年5月31日 上午11:57:10
 */
public class XproException implements Serializable {
    private static final long serialVersionUID = 4247532888629155074L;
    private static final Field STACKTRACE_ELEMENT_DECLARINGCLASS = ReflectionUtils.findField(StackTraceElement.class, "declaringClass", String.class);
    private String className;
    private String message;
    private String code;
    private LightStackTrace[] stackTraces;
    static {
        STACKTRACE_ELEMENT_DECLARINGCLASS.setAccessible(true);
    }

    public XproException() {
        super();
    }

    public XproException(Throwable e) {
        super();
        this.className = e.getClass().getName();
        this.message = e.getLocalizedMessage();
        StackTraceElement[] stackTraceList = e.getStackTrace();
        if (ArrayUtils.isNotEmpty(stackTraceList)) {
            final int length = stackTraceList.length;
            this.stackTraces = new LightStackTrace[length];
            for (int i = 0; i < length; i++) {
                StackTraceElement stackTraceElement = stackTraceList[i];
                LightStackTrace stackTrace = new LightStackTrace();
                String declaringClass = (String) ReflectionUtils.getField(STACKTRACE_ELEMENT_DECLARINGCLASS, stackTraceElement);
                stackTrace.setDeclaringClass(declaringClass);
                stackTrace.setFileName(stackTraceElement.getFileName());
                stackTrace.setLineNumber(stackTraceElement.getLineNumber());
                stackTrace.setMethodName(stackTraceElement.getMethodName());
                this.stackTraces[i] = stackTrace;
            }
        }
        if (e instanceof AppBizException) {
            this.code = ((AppBizException) e).getCode();
        }
        if (e instanceof AppRtException) {
            this.code = ((AppRtException) e).getCode();
        }
    }

    public StackTraceElement[] buildStackTraceElement() {
        final int length = this.stackTraces.length;
        StackTraceElement[] stackTraceList = new StackTraceElement[length];
        for (int i = 0; i < length; i++) {
            LightStackTrace stackTrace = this.stackTraces[i];
            stackTraceList[i] = new StackTraceElement(stackTrace.getDeclaringClass(), stackTrace.getMethodName(), stackTrace.getFileName(), stackTrace.getLineNumber());
        }
        return stackTraceList;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LightStackTrace[] getStackTraces() {
        return stackTraces;
    }

    public void setStackTraces(LightStackTrace[] stackTraces) {
        this.stackTraces = stackTraces;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
