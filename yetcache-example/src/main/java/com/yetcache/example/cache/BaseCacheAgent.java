package com.yetcache.example.cache;

import com.yetcache.core.key.CacheKeyExtractor;
import com.yetcache.core.CacheManager;
import com.yetcache.core.kv.MultiTierKVCache;
import com.yetcache.core.tenant.TenantProvider;
import lombok.Data;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author walter.yan
 * @since 2025/6/28
 */

@Data
public abstract class BaseCacheAgent<K, V> {
    @Autowired
    protected CacheManager cacheManager;
    @Autowired
    protected RedissonClient rClient;
    protected MultiTierKVCache<K, V> delegate;

    protected void createCache() {
        delegate = doCreateCache();
    }

    protected abstract MultiTierKVCache<K, V> doCreateCache();

    protected abstract String getCacheName();

    protected abstract CacheKeyExtractor<K, V> getBizKeyExtractor();

    public V get(K key) {
        return delegate.get(key);
    }

    public void put(K key, V value) {
        delegate.put(key, value);
    }

    public void invalidate(K key) {
        delegate.invalidate(key);
    }
}
