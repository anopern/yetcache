package com.yetcache.core.cache.singlehash;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.singlehash.CaffeineSingleHashCacheConfig;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.yetcache.core.util.CacheConstants.DEFAULT_EXPIRE;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class CaffeineSingleHashCache<V> {
    private final CaffeineSingleHashCacheConfig config;
    private final Cache<String, CacheValueHolder<V>> cache;

    public CaffeineSingleHashCache(CaffeineSingleHashCacheConfig config) {
        this.config = config;
        this.cache = buildCache();
    }

    private Cache<String, CacheValueHolder<V>> buildCache() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();

        if (config.getTtlSecs() != null) {
            builder.expireAfterWrite(DEFAULT_EXPIRE, TimeUnit.SECONDS);
        }
        if (config.getMaxSize() != null) {
            builder.maximumSize(config.getMaxSize());
        }

        return builder.build();
    }

    public CacheValueHolder<V> get(String field) {
        return cache.getIfPresent(field);
    }

    public void put(String field, CacheValueHolder<V> valueHolder) {
        cache.put(field, valueHolder);
    }

    public void invalidate(String field) {
        cache.invalidate(field);
    }

    public Map<String, CacheValueHolder<V>> listAll() {
        return cache.asMap();
    }
}
