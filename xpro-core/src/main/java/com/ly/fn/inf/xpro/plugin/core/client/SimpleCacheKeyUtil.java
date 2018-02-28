package com.ly.fn.inf.xpro.plugin.core.client;

import com.ly.fn.inf.util.JSONObject;

/**
 * 简单缓存键值工具类
 */
public class SimpleCacheKeyUtil {
    /**
     * 获得CacheKey
     */
    public static String getCacheKey(String serviceId, String method, Object[] args) {
        StringBuffer sb = new StringBuffer();
        sb.append(serviceId);
        sb.append(".");
        sb.append(method);
        sb.append("(");
        if (args != null) {
            boolean flag = false;
            for (Object arg : args) {
                if (flag) {
                    sb.append(",");
                }

                Object jsonObj = JSONObject.wrap(arg);
                sb.append(jsonObj.toString());
                flag = true;
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
