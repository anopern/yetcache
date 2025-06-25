package com.yetcache.core.kv;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public class CaffeineKVCache<K, V> extends EmbeddedKVCache<K, V> {
    protected Cache<K, V> cache;

    public CaffeineKVCache(Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public KVCacheGetResult<K, V> getWithResult(K key) {
        V v = cache.getIfPresent(key);
        return new KVCacheGetResult<>(v);
    }

    @Override
    public KVCacheResult putWithResult(K key, V value) {
        return null;
    }

    @Override
    public KVCacheResult invalidateWithResult(K key) {
        return null;
    }
}
