package com.yetcache.core.cache.kv;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.RedisCacheConfig;
import com.yetcache.core.support.util.TtlRandomizer;
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

    public CacheValueHolder<V> getIfPresent(String key) {
        RBucket<CacheValueHolder<V>> bucket = rClient.getBucket(key);
        return bucket.get();
    }

    public void put(String key, CacheValueHolder<V> value) {
        RBucket<CacheValueHolder<V>> bucket = rClient.getBucket(key);
        long realTtlSecs = TtlRandomizer.randomizeSecs(config.getTtlSecs(), config.getTtlRandomPct());
        bucket.set(value, realTtlSecs, TimeUnit.SECONDS);
    }

    public void invalidate(String key) {
        rClient.getBucket(key).delete();
    }
}
