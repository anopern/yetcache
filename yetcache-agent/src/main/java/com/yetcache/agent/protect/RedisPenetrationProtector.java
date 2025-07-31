//package com.yetcache.agent.protect;
//
//import com.yetcache.agent.interceptor.CacheAccessKey;
//import com.yetcache.agent.protect.cache.RedisPenetrationProtectCache;
//import org.redisson.api.RedissonClient;
//
///**
// * @author walter.yan
// * @since 2025/7/16
// */
//public class RedisPenetrationProtector implements PenetrationProtector {
//    private final RedisPenetrationProtectCache cache;
//
//    public RedisPenetrationProtector(RedissonClient redissonClient, String keyPrefix, String cacheName, long ttlSecs,
//                                     long maxSize) {
//        cache = new RedisPenetrationProtectCache(redissonClient, keyPrefix, cacheName, ttlSecs, maxSize);
//    }
//
//    @Override
//    public boolean isMarkedAsNull(CacheAccessKey key) {
//        return cache.contains(key.toString());
//    }
//
//    @Override
//    public void markAsNull(CacheAccessKey key) {
//        cache.add(key.toString());
//    }
//
//    public static RedisPenetrationProtector of(RedissonClient redissonClient, String keyPrefix, String cacheName,
//                                          long ttlSecs, long maxSize) {
//        return new RedisPenetrationProtector(redissonClient, keyPrefix, cacheName, ttlSecs, maxSize);
//    }
//}
