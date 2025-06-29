package com.yetcache.core.cache.singlehash;

import com.yetcache.core.cache.kv.CaffeineKVCache;
import com.yetcache.core.cache.kv.RedisKVCache;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import com.yetcache.core.config.singlehash.MultiTierSingleHashCacheConfig;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.key.CacheKeyConverter;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class MultiTierSingleHashCache<K, V> implements SingleHashCache<K, V> {

    private String cacheName;
    private final MultiTierSingleHashCacheConfig config;
    private final KVCacheLoader<K, V> cacheLoader;
    private CaffeineSingleHashCache<V> localCache;
    private RedisSingleHashCache<V> remoteCache;
    private CacheKeyConverter<K> keyConverter;
    private CaffeinePenetrationProtectCache<K> localPpCache;
    private RedisPenetrationProtectCache<K> remotePpCache;

    @Override
    public V get(K field) {
        return null;
    }

    @Override
    public void refresh(K field) {

    }

    @Override
    public void invalidate(K field) {

    }

    @Override
    public Map<K, V> listAll(boolean forceRefresh) {
        return null;
    }
}
