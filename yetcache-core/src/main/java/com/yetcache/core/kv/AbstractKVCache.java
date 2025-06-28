package com.yetcache.core.kv;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public abstract class AbstractKVCache<K, V> implements KVCache<K, V> {
    @Override
    public V get(K key) {
        return getWithResult(key).getValueHolder().getValue();
    }

    @Override
    public void put(K key, V value) {
        putWithResult(key, value);
    }

    @Override
    public void invalidate(K key) {
        invalidateWithResult(key);
    }

}
