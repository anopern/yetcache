package com.yetcache.core.cache.command;

import lombok.Getter;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public abstract class HashCacheCommand<K> {
    protected String cacheName;
    protected K key;

    protected Long localLogicTtlSecs;
    protected Long localPhysicalTtlSecs;
    protected Long remoteLogicTtlSecs;
    protected Long remotePhysicalTtlSecs;

    protected Map<String, String> metadata;

    public HashCacheCommand(String cacheName,
                            K key,
                            Long localLogicTtlSecs,
                            Long localPhysicalTtlSecs,
                            Long remoteLogicTtlSecs,
                            Long remotePhysicalTtlSecs,
                            Map<String, String> metadata) {
        this.cacheName = cacheName;
        this.key = key;
        this.localLogicTtlSecs = localLogicTtlSecs;
        this.localPhysicalTtlSecs = localPhysicalTtlSecs;
        this.remoteLogicTtlSecs = remoteLogicTtlSecs;
        this.remotePhysicalTtlSecs = remotePhysicalTtlSecs;
        this.metadata = metadata;
    }

    public abstract boolean isBatch();
}
