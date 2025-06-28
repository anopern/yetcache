package com.yetcache.example.cache;

import com.yetcache.core.CacheManager;
import com.yetcache.core.kv.MultiTierKVCache;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author walter.yan
 * @since 2025/6/28
 */

@Data
public abstract class BaseCacheAgent<K, V> {
    @Autowired
    protected CacheManager cacheManager;
    protected MultiTierKVCache<K, V> delegate;

    protected abstract void createCache();

    protected abstract String getCacheName();

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
