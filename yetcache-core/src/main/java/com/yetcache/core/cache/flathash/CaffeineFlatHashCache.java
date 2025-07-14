package com.yetcache.core.cache.flathash;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.flathash.CaffeineFlatHashCacheConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class CaffeineFlatHashCache<V> {
    private final Cache<String, ConcurrentHashMap<String, CacheValueHolder<V>>> cache;

    public CaffeineFlatHashCache(CaffeineFlatHashCacheConfig config) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Integer.MAX_VALUE, TimeUnit.SECONDS)
                .maximumSize(config.getMaxSize())
                .build();
    }

    public void putAll(String key, Map<String, CacheValueHolder<V>> valueHolderMap) {
        ConcurrentHashMap<String, CacheValueHolder<V>> map = cache.get(key, k -> new ConcurrentHashMap<>());
        map.putAll(valueHolderMap);
    }

    public Map<String, CacheValueHolder<V>> listAll(String key) {
        ConcurrentHashMap<String, CacheValueHolder<V>> map = cache.getIfPresent(key);
        return map != null ? new ConcurrentHashMap<>(map) : new ConcurrentHashMap<>();
    }
}

