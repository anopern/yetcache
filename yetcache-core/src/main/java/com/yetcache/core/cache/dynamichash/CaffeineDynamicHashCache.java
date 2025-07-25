package com.yetcache.core.cache.dynamichash;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.dynamichash.CaffeineDynamicHashCacheConfig;
import com.yetcache.core.support.util.TtlRandomizer;

import java.util.List;
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

    public Map<String, CacheValueHolder<V>> batchGet(String key, List<String> fields) {
        ConcurrentHashMap<String, CacheValueHolder<V>> fieldMap = cache.getIfPresent(key);
        if (fieldMap == null || fields == null || fields.isEmpty()) {
            return Map.of(); // 返回不可变空 map
        }

        Map<String, CacheValueHolder<V>> result = new ConcurrentHashMap<>();
        for (String field : fields) {
            CacheValueHolder<V> holder = fieldMap.get(field);
            if (holder != null) {
                result.put(field, holder);
            }
        }
        return result;
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

    public void removeAll(String key) {
        cache.invalidate(key);
    }

    public void remove(String key, String field) {
        ConcurrentHashMap<String, CacheValueHolder<V>> map = cache.getIfPresent(key);
        if (map != null) {
            map.remove(field);
        }
    }
}

