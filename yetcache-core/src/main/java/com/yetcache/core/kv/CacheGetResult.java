package com.yetcache.core.kv;

import com.yetcache.core.CacheTier;
import com.yetcache.core.CacheValueHolder;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public class CacheGetResult<K, V> extends CacheResult<K, V> {
    private CacheValueHolder<V> valueHolder;

    public CacheGetResult() {
    }

    public CacheGetResult(String cacheName, CacheTier cacheTier, K bizKey, String key) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.bizKey = bizKey;
        this.key = key;
    }
}
