package com.ly.fn.inf.xpro.plugin.core;


import com.ly.fn.inf.xpro.plugin.core.util.GuidUtils;

/**
 * Rpc调用跟踪器类。
 */
public class RpcCallTracker {
    protected static ThreadLocal<String> trackingCode = new ThreadLocal<String>();

    public static String getTrackingCode() {
        return trackingCode.get();
    }

    public static String newTrackingCode() {
        return GuidUtils.genGuid();
    }

    public static void setTrackingCode(String code) {
        MdcCaller.setTrackingCode(code);// 日志上下文记录当前线程的跟踪码
        trackingCode.set(code);
    }

    public static void cleanTrackingCode() {
        MdcCaller.removeTrackingCode();
        trackingCode.remove();
    }
}
