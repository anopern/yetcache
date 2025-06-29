package com.yetcache.core.cache.result;

import com.yetcache.core.config.CacheTier;
import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CacheGetResult<K, V> extends BaseKVCacheResult<K> {
    protected CacheValueHolder<V> valueHolder;

    public CacheGetResult() {
    }

    public CacheGetResult(String cacheName, CacheTier cacheTier, K bizKey, String key, Long startMills) {
        this.cacheName = cacheName;
        this.cacheTier = cacheTier;
        this.bizKey = bizKey;
        this.key = key;
        this.startMills = startMills;
    }

    public String toString() {
        return "CacheGetResult{" +
                "cacheName='" + cacheName + '\'' +
                ", cacheTier=" + cacheTier +
                ", bizKey=" + bizKey +
                ", key='" + key + '\'' +
                ", durationMills=" + durationMillis() +
                ", startMills=" + startMills +
                ", endMills=" + endMills +
                ", localStatus=" + localStatus +
                ", remoteStatus=" + remoteStatus +
                ", loadStatus=" + loadStatus +
                ", exception=" + exception + "}";
    }
}
