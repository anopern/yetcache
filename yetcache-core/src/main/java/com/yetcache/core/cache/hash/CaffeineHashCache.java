package com.yetcache.core.cache.hash;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.config.hash.CaffeineHashCacheConfig;
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
    private final Cache<String, ConcurrentHashMap<String, CacheValueHolder<?>>> cache;

    public CaffeineHashCache(CaffeineHashCacheConfig config) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(TtlRandomizer.randomizeSecs(config.getPhysicalTtlSecs(), config.getTtlRandomPct()),
                        TimeUnit.SECONDS)
                .maximumSize(config.getMaxSize())
                .build();
    }

    @SuppressWarnings("unchecked")
    public <T> CacheValueHolder<T> getIfPresent(String key, String field, TypeRef<?> valueTypeRef) {
        ConcurrentHashMap<String, CacheValueHolder<?>> map = cache.getIfPresent(key);
        if (map == null) {
            return null;
        }
        CacheValueHolder<?> holder = map.get(field);
        if (holder == null) {
            return null;
        }
        Object value = holder.getValue();
        if (value != null && !valueTypeRef.isInstance(value)) {
            throw new ClassCastException(
                    "Value type mismatch for key=" + key + ", field=" + field +
                            ", expected=" + valueTypeRef.getType() +
                            ", actual=" + value.getClass().getName()
            );
        }
        // 安全地转换成 CacheValueHolder<T>
        return (CacheValueHolder<T>) holder;
    }

    public <T> Map<String, CacheValueHolder<T>> batchGet(String key, List<String> fields, TypeRef<T> valueTypeRef) {
        ConcurrentHashMap<String, CacheValueHolder<?>> fieldMap = cache.getIfPresent(key);
        if (fieldMap == null || fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }

        // 取出期望的原始类做轻量校验（仅同 JVM 校验，非序列化）
        final Class<?> expected = valueTypeRef.getClass();

        Map<String, CacheValueHolder<T>> result = new ConcurrentHashMap<>(fields.size());
        for (String field : fields) {
            CacheValueHolder<?> holder = fieldMap.get(field);
            if (holder == null) continue;

            Object v = holder.getValue();
            if (v != null && !expected.isInstance(v)) {
                // 这里选择“跳过并告警”，也可以改成直接抛错
                // log.warn("Type mismatch for key={}, field={}, expected={}, actual={}",
                //          key, field, expected.getName(), v.getClass().getName());
                continue;
            }

            @SuppressWarnings("unchecked")
            CacheValueHolder<T> typed = (CacheValueHolder<T>) holder;
            result.put(field, typed);
        }
        return result;
    }


    public void put(String key, String field, CacheValueHolder<?> valueHolder) {
        ConcurrentHashMap<String, CacheValueHolder<?>> map = cache.get(key, k -> new ConcurrentHashMap<>());
        map.put(field, valueHolder);
    }

    public <T> void putAll(String key, Map<String, CacheValueHolder<T>> valueHolderMap) {
        ConcurrentHashMap<String, CacheValueHolder<?>> map = cache.get(key, k -> new ConcurrentHashMap<>());
        map.putAll(valueHolderMap);
    }

//    public Map<String, CacheValueHolder> listAll(String key) {
//        ConcurrentHashMap<String, CacheValueHolder> map = cache.getIfPresent(key);
//        return map != null ? new ConcurrentHashMap<>(map) : new ConcurrentHashMap<>();
//    }

    public void removeAll(String key) {
        cache.invalidate(key);
    }

    public void remove(String key, String field) {
        ConcurrentHashMap<String, CacheValueHolder<?>> map = cache.getIfPresent(key);
        if (map != null) {
            map.remove(field);
        }
    }

    public void batchRemove(String key, List<String> fields) {
        ConcurrentHashMap<String, CacheValueHolder<?>> map = cache.getIfPresent(key);
        if (map != null) {
            for (String field : fields) {
                map.remove(field);
            }
        }
    }
}

