package com.ly.fn.inf.xpro.plugin.core.cache;

import com.ly.fn.inf.cache.api.NotFoundCachedException;
import com.ly.fn.inf.xpro.plugin.core.client.SimpleCacheKeyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求范围缓存服务实现类。
 */
public class RequestScopeCacheService {
    private static ThreadLocal<Map<String, Object>> cacheHolder = new ThreadLocal<Map<String, Object>>();

    public static void initCache() {
        Map<String, Object> cache = new HashMap<String, Object>();
        cacheHolder.set(cache);
    }

    public static void clearCache() {
        cacheHolder.remove();
    }

    public static Object getCachedReturn(String serviceId, String method, Object[] args) throws NotFoundCachedException {
        Map<String, Object> cache = cacheHolder.get();
        if (cache == null) {
            throw new NotFoundCachedException();
        }

        String key = SimpleCacheKeyUtil.getCacheKey(serviceId, method, args);
        if (cache.containsKey(key) == false) {
            throw new NotFoundCachedException();
        }

        return cache.get(key);
    }

    public static void cacheReturn(String serviceId, String method, Object[] args, Object returnObj) {
        Map<String, Object> cache = cacheHolder.get();
        if (cache == null) {
            return;
        }

        String key = SimpleCacheKeyUtil.getCacheKey(serviceId, method, args);
        cache.put(key, returnObj);
    }

}
