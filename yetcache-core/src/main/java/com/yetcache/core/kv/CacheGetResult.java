package com.yetcache.core.kv;

import com.yetcache.core.CacheTier;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public class CacheGetResult<K, V> extends CacheResult<K, V> {

    public CacheGetResult() {
    }

    public CacheGetResult(String cacheName, CacheTier cacheTier, K bizKey, String key) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.bizKey = bizKey;
        this.key = key;
    }

    @Override
    public String toString() {
        return "CacheGetResult{" +
                "cacheName='" + cacheName + '\'' +
                ", cacheTier=" + cacheTier +
                ", bizKey=" + bizKey +
                ", key='" + key + '\'' +
                ", localStatus=" + localStatus +
                ", remoteStatus=" + remoteStatus +
                ", loadStatus=" + loadStatus +
                ", valueHolder=" + valueHolder +
                ", exception=" + exception +
                '}';
    }
}
