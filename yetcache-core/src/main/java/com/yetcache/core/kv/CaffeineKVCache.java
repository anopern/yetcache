package com.yetcache.core.kv;

import com.github.benmanes.caffeine.cache.Cache;
import com.yetcache.core.config.GlobalConfig;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public class CaffeineKVCache<K, V> extends AbstractdKVCache<K, V> {
    protected  GlobalConfig globalConfig;
    protected Cache<K, V> cache;


    @Override
    public KVCacheGetResult<K, V> getWithResult(K key) {
        V v = cache.getIfPresent(key);
        return new KVCacheGetResult<>(v);
    }

    @Override
    public KVCacheResult putWithResult(K key, V value) {
        cache.put(key, value);
        return new KVCacheResult();
    }

    @Override
    public KVCacheResult invalidateWithResult(K key) {
        cache.invalidate(key);
        return new KVCacheResult();
    }
}
