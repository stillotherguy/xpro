package com.ly.fn.inf.xpro.plugin.core.protocol;

/**
 * 轻量级StackTraceElement类。
 */
public class LightStackTraceElement {
    /**
     * 声明类名称
     */
    private String declaringClass;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 行数
     */
    private int lineNumber;

    public String getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
