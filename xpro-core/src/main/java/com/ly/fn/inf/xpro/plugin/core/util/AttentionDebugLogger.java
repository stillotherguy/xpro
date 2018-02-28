package com.ly.fn.inf.xpro.plugin.core.util;

import org.slf4j.Logger;

/**
 * 根据开关模式记录日志信息
 */
public class AttentionDebugLogger {

    /**
     * DEBUG 模式
     */
    public static volatile boolean DEBUGMODE = false;

    public static void log(Logger logger, String msg, Object... params) {
        if (!DEBUGMODE) {
            return;
        }
        logger.info(msg, params);
    }
}
