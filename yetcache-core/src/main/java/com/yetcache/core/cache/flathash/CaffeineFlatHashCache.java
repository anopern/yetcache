package com.yetcache.core.cache.flathash;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.flathash.CaffeineFlatHashCacheConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yetcache.core.util.CacheConstants.DEFAULT_EXPIRE;
import static com.yetcache.core.util.CacheConstants.DEFAULT_LOCAL_LIMIT;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class CaffeineFlatHashCache<V> {
    private final CaffeineFlatHashCacheConfig config;
    private final Cache<String, ConcurrentHashMap<String, CacheValueHolder<V>>> cache;

    public CaffeineFlatHashCache(CaffeineFlatHashCacheConfig config) {
        this.config = config;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(DEFAULT_EXPIRE, TimeUnit.SECONDS)
                .maximumSize(config.getMaxSize())
                .build();
    }

    public CacheValueHolder<V> getIfPresent(String key, String field) {
        ConcurrentHashMap<String, CacheValueHolder<V>> map = cache.getIfPresent(key);
        return map != null ? map.get(field) : null;
    }

    public void put(String key, String field, CacheValueHolder<V> valueHolder) {
        ConcurrentHashMap<String, CacheValueHolder<V>> map = cache.get(key, k -> new ConcurrentHashMap<>());
        map.put(field, valueHolder);
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

