package com.yetcache.core.kv;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.core.config.CaffeineCacheConfig;
import com.yetcache.core.config.GlobalConfig;
import com.yetcache.core.config.SyncConfig;

import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public class CaffeineKVCache<K, V> extends AbstractdKVCache<K, V> {
    protected final GlobalConfig globalConfig;
    protected final CaffeineCacheConfig localConfig;
    protected final SyncConfig syncConfig;
    protected Cache<K, V> cache;

    public CaffeineKVCache(GlobalConfig globalConfig, CaffeineCacheConfig localConfig, SyncConfig syncConfig) {
        this.globalConfig = globalConfig;
        this.localConfig = localConfig;
        this.syncConfig = syncConfig;

        Long ttlSecs = localConfig.getTtlSec() != null ? localConfig.getTtlSec() : globalConfig.getLocalTtlSec();
        cache = Caffeine.newBuilder()
                .maximumSize(localConfig.getMaxSize())
                .expireAfterWrite(ttlSecs, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public KVCacheGetResult<K, V> getWithResult(K key) {
        V v = cache.getIfPresent(key);
        return new KVCacheGetResult<>(v);
    }

    @Override
    public KVCacheResult putWithResult(K key, V value) {
        return null;
    }

    @Override
    public KVCacheResult invalidateWithResult(K key) {
        return null;
    }
}
