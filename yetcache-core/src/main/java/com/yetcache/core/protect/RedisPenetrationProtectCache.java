//package com.yetcache.core.protect;
//
//import org.redisson.api.RBucket;
//import org.redisson.api.RedissonClient;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author walter.yan
// * @since 2025/6/28
// */
//public class RedisPenetrationProtectCache extends AbstractPenetrationProtectCache {
//    private final RedissonClient rClient;
//    private final long ttlSeconds;
//
//    public RedisPenetrationProtectCache(RedissonClient rClient, String keyPrefix, String cacheName,
//                                        long ttlSeconds, long maxSize) {
//        super(keyPrefix, cacheName);
//        this.rClient = rClient;
//        this.ttlSeconds = ttlSeconds;
//    }
//
//    @Override
//    public void markNotFund(String logicKey) {
//        RBucket<Integer> bucket = rClient.getBucket(buildKey(logicKey));
//        bucket.set(1, ttlSeconds, TimeUnit.SECONDS);
//    }
//
//    @Override
//    public boolean isBlocked(String logicKey) {
//        RBucket<Integer> bucket = rClient.getBucket(buildKey(logicKey));
//        return bucket.isExists();
//    }
//}
