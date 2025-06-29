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
public class CachePutResult<K, V> extends CacheResult<K> {
    protected CacheValueHolder<V> valueHolder;

    public CachePutResult(String cacheName, CacheTier cacheTier, K bizKey, String key) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.bizKey = bizKey;
        this.key = key;
    }
}
