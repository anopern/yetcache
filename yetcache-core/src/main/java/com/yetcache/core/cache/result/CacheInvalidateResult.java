package com.yetcache.core.cache.result;

import com.yetcache.core.config.CacheTier;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class CacheInvalidateResult<K> extends BaseKVCacheResult<K> {
    public CacheInvalidateResult(String cacheName, CacheTier cacheTier, K bizKey, String key, Long startMills) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.bizKey = bizKey;
        this.key = key;
        this.startMills = startMills;
    }
}
