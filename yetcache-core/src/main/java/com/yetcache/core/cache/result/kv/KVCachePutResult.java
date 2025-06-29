package com.yetcache.core.cache.result.kv;

import com.yetcache.core.cache.result.kv.BaseKVCacheResult;
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
public class KVCachePutResult<K, V> extends BaseKVCacheResult<K> {
    protected CacheValueHolder<V> valueHolder;

    public KVCachePutResult(String cacheName, CacheTier cacheTier, K bizKey, String key) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.bizKey = bizKey;
        this.key = key;
    }
}
