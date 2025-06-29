package com.yetcache.core.cache.result.singlehash;

import com.yetcache.core.cache.result.BaseCacheResult;
import com.yetcache.core.cache.result.kv.KVCacheGetResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.CacheTier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class SingleHashCacheGetResult<K, V> extends BaseSingleHashCacheResult<K> {
    protected CacheValueHolder<V> valueHolder;

    public SingleHashCacheGetResult(String cacheName, CacheTier cacheTier, String key, K bizField, String field,
                                    Long startMills) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.key = key;
        this.bizField = bizField;
        this.field = field;
        this.startMills = startMills;
    }

}
