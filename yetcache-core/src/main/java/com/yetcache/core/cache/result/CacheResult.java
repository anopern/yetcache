package com.yetcache.core.cache.result;

import com.yetcache.core.config.CacheTier;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
@Data
public class CacheResult<K> {
    protected String cacheName;
    protected CacheTier cacheTier;
    protected K bizKey;
    protected String key;
    protected CacheAccessStatus localStatus;
    protected CacheAccessStatus remoteStatus;
    protected SourceLoadStatus loadStatus;
    protected Exception exception;
    protected Long startMills;
    protected Long endMills;

    public void start() {
        this.startMills = System.currentTimeMillis();
    }

    public CacheResult<K> end() {
        this.endMills = System.currentTimeMillis();
        return this;
    }

    public long durationMillis() {
        return endMills - startMills;
    }
}
