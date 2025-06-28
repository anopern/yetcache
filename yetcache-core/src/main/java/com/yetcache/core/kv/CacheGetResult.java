package com.yetcache.core.kv;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public class CacheGetResult<K, V> extends CacheResult<K> {
    private K bizKey;
    private V value;

    public CacheGetResult(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public static <K, V> CacheGetResult<K, V> localHit(K bizKey, V value) {
        return new CacheGetResult<>(value);
    }

    public static <K, V> CacheGetResult<K, V> remoteHit(K bizKey, V value) {
        return new CacheGetResult<>(value);
    }

    public static <K, V> CacheGetResult<K, V> missThenLoad(K bizKey, V value) {
        return new CacheGetResult<>(value);
    }

    public static <K, V> CacheGetResult<K, V> notFound(K bizKey) {
        return new CacheGetResult<>(null);
    }
}
