package com.yetcache.core.cache.result;

import com.yetcache.core.config.CacheTier;
import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CacheRefreshResult<K, V> extends BaseKVCacheResult<K> {
    protected CacheValueHolder<V> valueHolder;

    public CacheRefreshResult(String cacheName, CacheTier cacheTier, K bizKey, String key, Long startMills) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.bizKey = bizKey;
        this.key = key;
        this.startMills = startMills;
    }
}
