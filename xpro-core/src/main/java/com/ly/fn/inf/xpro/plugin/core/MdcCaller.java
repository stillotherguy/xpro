package com.ly.fn.inf.xpro.plugin.core;

import org.slf4j.MDC;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 * 
 * @version 1.0.0
 * @since 2016年1月19日 下午10:27:53
 */
public class MdcCaller {

    private static final String TRACKING_CODE_KEY = "trackingCode";

    public static void setTrackingCode(String value) {
        MDC.put(TRACKING_CODE_KEY, value);
    }
    
    public static void removeTrackingCode() {
        MDC.remove(TRACKING_CODE_KEY);
    }
}