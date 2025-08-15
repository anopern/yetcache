package com.yetcache.core.cache.hash;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.dynamichash.CaffeineDynamicHashCacheConfig;
import com.yetcache.core.support.util.TtlRandomizer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class CaffeineHashCache {
    private final Cache<String, ConcurrentHashMap<String, CacheValueHolder>> cache;

    public CaffeineHashCache(CaffeineDynamicHashCacheConfig config) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(TtlRandomizer.randomizeSecs(config.getPhysicalTtlSecs(), config.getTtlRandomPct()),
                        TimeUnit.SECONDS)
                .maximumSize(config.getMaxSize())
                .build();
    }

    public CacheValueHolder getIfPresent(String key, String field) {
        ConcurrentHashMap<String, CacheValueHolder> map = cache.getIfPresent(key);
        if (map == null) {
            return null;
        }
        return map.get(field);
    }

    public Map<String, CacheValueHolder> batchGet(String key, List<String> fields) {
        ConcurrentHashMap<String, CacheValueHolder> fieldMap = cache.getIfPresent(key);
        if (fieldMap == null || fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, CacheValueHolder> result = new ConcurrentHashMap<>();
        for (String field : fields) {
            CacheValueHolder holder = fieldMap.get(field);
            if (holder != null) {
                result.put(field, holder);
            }
        }
        return result;
    }


    public void put(String key, String field, CacheValueHolder valueHolder) {
        ConcurrentHashMap<String, CacheValueHolder> map = cache.get(key, k -> new ConcurrentHashMap<>());
        map.put(field, valueHolder);
    }

    public void putAll(String key, Map<String, CacheValueHolder> valueHolderMap) {
        ConcurrentHashMap<String, CacheValueHolder> map = cache.get(key, k -> new ConcurrentHashMap<>());
        map.putAll(valueHolderMap);
    }

    public Map<String, CacheValueHolder> listAll(String key) {
        ConcurrentHashMap<String, CacheValueHolder> map = cache.getIfPresent(key);
        return map != null ? new ConcurrentHashMap<>(map) : new ConcurrentHashMap<>();
    }

    public void removeAll(String key) {
        cache.invalidate(key);
    }

    public void remove(String key, String field) {
        ConcurrentHashMap<String, CacheValueHolder> map = cache.getIfPresent(key);
        if (map != null) {
            map.remove(field);
        }
    }
}

