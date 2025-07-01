package com.yetcache.core.cache.result.flathash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.CacheTier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class FlatHashCacheGetResult<K, V> extends BaseSingleHashCacheResult<K> {
    protected CacheValueHolder<V> valueHolder;
    protected Map<K, CacheValueHolder<V>> valueHolderMap;

    public FlatHashCacheGetResult(String cacheName, CacheTier cacheTier, String key, K bizField, String field,
                                  Long startMills) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.key = key;
        this.bizField = bizField;
        this.field = field;
        this.startMills = startMills;
    }

    public FlatHashCacheGetResult(String cacheName, CacheTier cacheTier, String key, Long startMills) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.key = key;
        this.startMills = startMills;
    }

}
