package com.yetcache.core.kv;

import com.yetcache.core.CacheTier;
import com.yetcache.core.CacheValueHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CacheGetResult<K, V> extends CacheResult<K> {
    protected CacheValueHolder<V> valueHolder;

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
