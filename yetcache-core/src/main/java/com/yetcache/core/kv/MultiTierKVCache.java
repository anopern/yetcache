package com.yetcache.core.kv;

import com.yetcache.core.config.MultiTierCacheConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MultiTierKVCache<K, V> extends AbstractKVCache<K, V> {
    private MultiTierCacheConfig config;

    public MultiTierKVCache(MultiTierCacheConfig config) {
        this.config = config;
    }

    @Override
    public KVCacheGetResult<K, V> getWithResult(K key) {
        return null;
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
