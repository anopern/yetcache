package com.yetcache.core.cache.kv;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.kv.RedisKVCacheConfig;
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
public class RedisKVCache {

    protected final RedisKVCacheConfig config;
    protected final RedissonClient rClient;

    public RedisKVCache(RedisKVCacheConfig config, RedissonClient rClient) {
        this.config = config;
        this.rClient = rClient;
    }

    public <T> CacheValueHolder<T> getIfPresent(String key) {
        RBucket<CacheValueHolder<T>> bucket = rClient.getBucket(key);
        return bucket.get();
    }

    public <T> void put(String key, CacheValueHolder<T> value) {
        RBucket<CacheValueHolder<T>> bucket = rClient.getBucket(key);
        long realTtlSecs = TtlRandomizer.randomizeSecs(config.getTtlSecs(), config.getTtlRandomPct());
        bucket.set(value, realTtlSecs, TimeUnit.SECONDS);
    }

    public void remove(String key) {
        rClient.getBucket(key).delete();
    }
}
