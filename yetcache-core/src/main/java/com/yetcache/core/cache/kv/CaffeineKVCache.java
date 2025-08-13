//package com.yetcache.core.cache.kv;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.yetcache.core.cache.support.CacheValueHolder;
//import com.yetcache.core.config.kv.CaffeineKVCacheConfig;
//import com.yetcache.core.support.util.TtlRandomizer;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * 一级本地缓存组件，仅负责本地 KV 操作。
// * 不负责可观测性、不记录 cacheName 状态，仅用于执行。
// * 所有上下文信息（如 cacheName）均由调用方传入，仅用于日志。
// *
// * @author walter.yan
// * @since 2025/6/25
// */
//@Slf4j
//public class CaffeineKVCache<V> {
//
//    protected final CaffeineKVCacheConfig config;
//    protected final Cache<String, CacheValueHolder<V>> cache;
//
//    public CaffeineKVCache(CaffeineKVCacheConfig config) {
//        this.config = config;
//        this.cache = buildCache();
//    }
//
//    private Cache<String, CacheValueHolder<V>> buildCache() {
//        Caffeine<Object, Object> builder = Caffeine.newBuilder();
//
//        if (config.getTtlSecs() != null) {
//            long realTtl = TtlRandomizer.randomizeSecs(config.getTtlSecs(), config.getTtlRandomPct());
//            builder.expireAfterWrite(realTtl, TimeUnit.SECONDS);
//        }
//        if (config.getMaxSize() != null) {
//            builder.maximumSize(config.getMaxSize());
//        }
//
//        return builder.build();
//    }
//
//    /**
//     * 获取缓存值。仅在命中时返回非 null。
//     *
//     * @param key 访问的 key
//     */
//    public CacheValueHolder<V> getIfPresent(String key) {
//        return cache.getIfPresent(key);
//    }
//
//    /**
//     * 写入缓存值。
//     *
//     * @param key   key
//     * @param value value
//     */
//    public void put(String key, CacheValueHolder<V> value) {
//        cache.put(key, value);
//    }
//
//    /**
//     * 使缓存失效。
//     *
//     * @param key 要删除的 key
//     */
//    public void invalidate(String key) {
//        cache.invalidate(key);
//    }
//}
