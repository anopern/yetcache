package com.yetcache.core.kv;

import com.yetcache.core.config.RedisCacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 二级远程缓存组件，仅封装基础 KV 操作。
 * 不负责观测性、不持有 cacheName，所有上下文需由上层传入。
 *
 * @author walter.yan
 * @since 2025/6/25
 */
@Slf4j
public class RedisKVCache<V> {

    protected final RedisCacheConfig config;
    protected final RedissonClient rClient;

    public RedisKVCache(RedisCacheConfig config, RedissonClient rClient) {
        this.config = config;
        this.rClient = rClient;
    }

    public V get(String key) {
        RBucket<V> bucket = rClient.getBucket(key);
        return bucket.get();
    }

    public void put(String key, V value) {
        RBucket<V> bucket = rClient.getBucket(key);
        bucket.set(value, config.getTtlSecs(), TimeUnit.SECONDS);
    }

    public void invalidate(String key) {
        rClient.getBucket(key).delete();
    }
}
