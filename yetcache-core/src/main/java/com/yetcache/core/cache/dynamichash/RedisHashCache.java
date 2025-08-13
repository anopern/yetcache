package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.CacheValueHolderCodec;
import com.yetcache.core.cache.TypeDescriptor;
import com.yetcache.core.cache.ValueCodec;
import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.ByteArrayCodec;
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
    private final TypeDescriptor typeDesc;
    private final CacheValueHolderCodec holderCodec;

    public RedisHashCache(RedissonClient rClient,
                          TypeDescriptor typeDesc,
                          ValueCodec codec) {
        this.rClient = rClient;
        this.typeDesc = typeDesc;

        this.holderCodec = new CacheValueHolderCodec(codec);
    }

    private RMap<String, byte[]> map(String key) {
        return rClient.getMap(key, new CompositeCodec(StringCodec.INSTANCE, ByteArrayCodec.INSTANCE));
    }

    public CacheValueHolder getIfPresent(String key, String field) {
        byte[] raw = map(key).get(field);
        if (null == raw) {
            return null;
        }
        try {
            return (CacheValueHolder) holderCodec.decode(raw, typeDesc.getValueTypeRef().getType());
        } catch (Exception e) {
            throw new IllegalStateException("decode failed", e);
        }
    }

    public Map<String, CacheValueHolder> batchGet(String key, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, byte[]> rawMap = map(key).getAll(new HashSet<>(fields));
        if (rawMap == null || rawMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, CacheValueHolder> resultMap = new HashMap<>(rawMap.size());
        for (Map.Entry<String, byte[]> e : rawMap.entrySet()) {
            byte[] raw = e.getValue();
            if (raw != null) {
                try {
                    CacheValueHolder holder = (CacheValueHolder) holderCodec.decode(raw,
                            typeDesc.getValueTypeRef().getType());
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

    public void putAll(String key, Map<String, CacheValueHolder> holderMap, long physicalTtlSecs) {
        if (holderMap == null || holderMap.isEmpty()) {
            return;
        }

        Map<String, byte[]> rawMap = new HashMap<>(holderMap.size());
        for (Map.Entry<String, CacheValueHolder> e : holderMap.entrySet()) {
            try {
                rawMap.put(e.getKey(), holderCodec.encode(e.getValue(), typeDesc.getValueTypeRef().getType()));
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
