package com.yetcache.core.cache.dynamichash;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.dynamichash.CaffeineDynamicHashCacheConfig;
import com.yetcache.core.support.util.TtlRandomizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class CaffeineDynamicHashCache<V> {
    private final CaffeineDynamicHashCacheConfig config;
    private final Cache<String, ConcurrentHashMap<String, CacheValueHolder<V>>> cache;

    public CaffeineDynamicHashCache(CaffeineDynamicHashCacheConfig config) {
        this.config = config;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(TtlRandomizer.randomizeSecs(config.getTtlSecs(), config.getTtlRandomPct()), TimeUnit.SECONDS)
                .maximumSize(config.getMaxSize())
                .build();
    }

    public CacheValueHolder<V> getIfPresent(String key, String field) {
        ConcurrentHashMap<String, CacheValueHolder<V>> map = cache.getIfPresent(key);
        if (map == null) {
            return null;
        }
        return map.get(field);
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

    public void invalidateAll(String key) {
        cache.invalidate(key);
    }

    public void invalidate(String key, String field) {
        ConcurrentHashMap<String, CacheValueHolder<V>> map = cache.getIfPresent(key);
        if (map != null) {
            map.remove(field);
        }
    }
}

