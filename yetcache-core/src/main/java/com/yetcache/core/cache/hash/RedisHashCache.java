package com.yetcache.core.cache.hash;

import com.yetcache.core.codec.*;
import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 二级远程缓存组件，仅封装基础 KV 操作。
 * 不负责观测性、不持有 cacheName，所有上下文需由上层传入。
 *
 * @author walter.yan
 * @since 2025/6/25
 */
@Slf4j
public class RedisHashCache {
    protected final RedissonClient rClient;
    private final JsonValueCodec codec;

    public RedisHashCache(RedissonClient rClient,
                          JsonValueCodec codec) {
        this.rClient = rClient;
        this.codec = codec;
    }

    private RMap<String, String> map(String key) {
        return rClient.getMap(key, new CompositeCodec(StringCodec.INSTANCE, StringCodec.INSTANCE));
    }

    public <T> CacheValueHolder<T> get(String key, String field, TypeRef<T> valueTypeRef) {
        String json = map(key).get(field);
        if (null == json) {
            return null;
        }
        try {
            TypeRef<CacheValueHolder<T>> holderRef = TypeRefs.holderOf(valueTypeRef);
            return codec.decode(json, holderRef.getType());
        } catch (Exception e) {
            throw new IllegalStateException("decode failed", e);
        }
    }

    public <T> Map<String, CacheValueHolder<T>> batchGet(String key, List<String> fields, TypeRef<?> valueTypeRef) {
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> rawMap = map(key).getAll(new HashSet<>(fields));
        if (rawMap == null || rawMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, CacheValueHolder<T>> resultMap = new HashMap<>(rawMap.size());
        for (Map.Entry<String, String> e : rawMap.entrySet()) {
            String json = e.getValue();
            if (json != null) {
                try {
                    CacheValueHolder<T> holder = codec.decode(json, valueTypeRef.getType());
                    resultMap.put(e.getKey(), holder);
                } catch (Exception ex) {
                    log.warn("decode cache value failed, key: " + e.getKey());
                }
            }
        }

        return resultMap;
    }

//    public Map<String, CacheValueHolder<V>> listAll(String key) {
//        RMap<String, CacheValueHolder<V>> map = rClient.getMap(key);
//        return map.readAllMap();
//    }

    public <T> void putAll(String key, Map<String, CacheValueHolder<T>> holderMap, long physicalTtlSecs) {
        if (holderMap == null || holderMap.isEmpty()) {
            return;
        }

        Map<String, String> rawMap = new HashMap<>(holderMap.size());
        for (Map.Entry<String, CacheValueHolder<T>> e : holderMap.entrySet()) {
            try {
                rawMap.put(e.getKey(), codec.encode(e.getValue()));
            } catch (Exception ex) {
                log.warn("序列化字段失败：fieldKey={}, err={}", e.getKey(), ex.getMessage(), ex);
            }
        }

        map(key).putAll(rawMap);
        map(key).expire(physicalTtlSecs, TimeUnit.SECONDS);
    }

//    public void invalidate(String key, String field) {
//        Map<String, CacheValueHolder<V>> rmap = rClient.getMap(key);
//        rmap.remove(field);
//    }
//
//    public void invalidateAll(String key) {
//        RMap<String, CacheValueHolder<V>> map = rClient.getMap(key);
//        map.clear();
//    }
}
