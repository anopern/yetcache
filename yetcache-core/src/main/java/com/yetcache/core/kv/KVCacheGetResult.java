package com.yetcache.core.kv;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public class KVCacheGetResult<K, V> extends KVCacheResult {
    private V value;

    public KVCacheGetResult(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
