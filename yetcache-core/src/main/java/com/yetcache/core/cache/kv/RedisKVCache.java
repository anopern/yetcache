package com.yetcache.core.cache.kv;

import com.yetcache.core.support.CacheValueHolder;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.codec.TypeRefs;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.time.Duration;

/**
 * 二级远程缓存组件，仅封装基础 KV 操作。
 * 不负责观测性、不持有 cacheName，所有上下文需由上层传入。
 *
 * @author walter.yan
 * @since 2025/6/25
 */
@Slf4j
public class RedisKVCache {

    protected final RedissonClient rClient;
    private final JsonValueCodec codec;

    public RedisKVCache(RedissonClient rClient, JsonValueCodec codec) {
        this.rClient = rClient;
        this.codec = codec;
    }

    private RBucket<String> bucket(String key) {
        return rClient.getBucket(key, StringCodec.INSTANCE);
    }

    public <T> CacheValueHolder<T> get(String key, TypeRef<T> valueTypeRef) {
        String json = bucket(key).get();
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

    public <T> void put(String key, CacheValueHolder<T> valueHolder, long physicalTtlSecs) {
        if (null == key || key.trim().length() == 0) {
            throw new IllegalArgumentException("key is blank");
        }
        if (null == valueHolder) {
            throw new IllegalArgumentException("value is null");
        }
        if (physicalTtlSecs <= 0) {
            throw new IllegalArgumentException("ttl is invalid");
        }
        try {
            RBucket<String> bucket = bucket(key);
            bucket.set(codec.encode(valueHolder));
            bucket.expire(Duration.ofSeconds(physicalTtlSecs));
        } catch (Exception ex) {
            log.warn("序列化字段失败：key={}, err={}", key, ex.getMessage(), ex);
        }
    }

    public void remove(String key) {
        rClient.getBucket(key).delete();
    }
}
