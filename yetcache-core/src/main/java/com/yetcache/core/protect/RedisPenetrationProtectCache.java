package com.yetcache.core.protect;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class RedisPenetrationProtectCache<K> extends AbstractPenetrationProtectCache<K> {
    private final RedissonClient rClient;
    private final long ttlSeconds;
    private final long maxSize;

    public RedisPenetrationProtectCache(RedissonClient rClient, String keyPrefix, String cacheName,
                                        long ttlSeconds, long maxSize) {
        super(keyPrefix, cacheName);
        this.rClient = rClient;
        this.ttlSeconds = ttlSeconds;
        this.maxSize = maxSize;
    }

    @Override
    public void markMiss(K bizKey) {
        RBucket<Integer> bucket = rClient.getBucket(buildKey(bizKey));
        bucket.set(1, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean isBlocked(K bizKey) {
        RBucket<Integer> bucket = rClient.getBucket(buildKey(bizKey));
        return bucket.isExists();
    }
}
