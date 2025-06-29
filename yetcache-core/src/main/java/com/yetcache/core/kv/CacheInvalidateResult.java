package com.yetcache.core.kv;

import com.yetcache.core.CacheTier;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class CacheInvalidateResult<K> extends CacheResult<K> {
    public CacheInvalidateResult(String cacheName, CacheTier cacheTier, K bizKey, String key, Long startMills) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.bizKey = bizKey;
        this.key = key;
        this.startMills = startMills;
    }
}
