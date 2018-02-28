package com.ly.fn.inf.xpro.plugin.core.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常数据类。
 */
public class ExceptionData {
    /**
     * 堆栈信息
     */
    private List<LightStackTraceElement[]> stackTrace;

    /**
     * 异常消息
     */
    private List<String> messages;

    public ExceptionData() {}

    private void dumpStackTrace(Throwable e) {
        if (e.getCause() != null) {
            dumpStackTrace(e.getCause());
        }

        LightStackTraceElement[] lightTrace = new LightStackTraceElement[e.getStackTrace().length];
        int i = 0;
        for (StackTraceElement element : e.getStackTrace()) {
            lightTrace[i] = new LightStackTraceElement();
            lightTrace[i].setDeclaringClass(element.getClassName());
            lightTrace[i].setFileName(element.getFileName());
            lightTrace[i].setLineNumber(element.getLineNumber());
            lightTrace[i].setMethodName(element.getMethodName());

            i++;
        }

        messages.add(e.getMessage());
        stackTrace.add(lightTrace);
    }

    public ExceptionData(Throwable e) {
        messages = new ArrayList<String>();
        stackTrace = new ArrayList<LightStackTraceElement[]>();
        dumpStackTrace(e);
    }

    public List<LightStackTraceElement[]> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<LightStackTraceElement[]> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    private StackTraceElement[] getRegularStackTrace(LightStackTraceElement[] lightTrace) {
        if (stackTrace == null) {
            return new StackTraceElement[0];
        }

        StackTraceElement[] rStackTrace = new StackTraceElement[lightTrace.length];
        int i = 0;
        for (LightStackTraceElement element : lightTrace) {
            rStackTrace[i++] = new StackTraceElement(element.getDeclaringClass(), element.getMethodName(), element.getFileName(), element.getLineNumber());
        }

        return rStackTrace;
    }

    public Throwable toException() {
        Throwable exception = null;
        for (int i = 0; i < stackTrace.size(); i++) {
            StackTraceElement[] rStackTrace = getRegularStackTrace(stackTrace.get(i));
            String message = messages.get(i);

            if (exception != null) {
                exception = new Throwable(message, exception);
            } else {
                exception = new Throwable(message);
            }

            exception.setStackTrace(rStackTrace);
        }

        return exception;
    }
}
