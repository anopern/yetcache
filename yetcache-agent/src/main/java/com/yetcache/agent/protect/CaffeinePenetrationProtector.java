package com.yetcache.agent.protect;

import com.yetcache.agent.interceptor.CacheAccessKey;
import com.yetcache.agent.protect.cache.CaffeinePenetrationProtectCache;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public class CaffeinePenetrationProtector implements PenetrationProtector {
    private final CaffeinePenetrationProtectCache cache;

    public CaffeinePenetrationProtector(String keyPrefix, String cacheName, long ttlSecs, long maxSize) {
        cache = new CaffeinePenetrationProtectCache(keyPrefix, cacheName, ttlSecs, maxSize);
    }

    @Override
    public boolean isMarkedAsNull(CacheAccessKey key) {
        return cache.contains(key.toString());
    }

    @Override
    public void markAsNull(CacheAccessKey key) {
        cache.add(key.toString());
    }

    public static CaffeinePenetrationProtector of(String keyPrefix, String cacheName, long ttlSecs, long maxSize) {
        return new CaffeinePenetrationProtector(keyPrefix, cacheName, ttlSecs, maxSize);
    }
}
