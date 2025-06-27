//package com.yetcache.core.kv;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.yetcache.core.config.GlobalConfig;
//import com.yetcache.core.config.SyncConfig;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * @author walter.yan
// * @since 2025/6/25
// */
//public class CaffeineKVCache<K, V> extends AbstractdKVCache<K, V> {
//    protected final GlobalConfig globalConfig;
//    protected Cache<K, V> cache;
//
//
//    @Override
//    public KVCacheGetResult<K, V> getWithResult(K key) {
//        V v = cache.getIfPresent(key);
//        return new KVCacheGetResult<>(v);
//    }
//
//    @Override
//    public KVCacheResult putWithResult(K key, V value) {
//        cache.put(key, value);
//        return new KVCacheResult();
//    }
//
//    @Override
//    public KVCacheResult invalidateWithResult(K key) {
//        cache.invalidate(key);
//        return new KVCacheResult();
//    }
//}
