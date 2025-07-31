package com.yetcache.core.cache.command;

import lombok.Getter;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public class SingleHashCachePutCommand<K, F, V> extends HashCacheCommand<K> {
    private final F field;
    private final V value;

    public SingleHashCachePutCommand(String cacheName,
                                     K key,
                                     F field,
                                     V value,
                                     Long localLogicTtlSecs,
                                     Long localPhysicalTtlSecs,
                                     Long remoteLogicTtlSecs,
                                     Long remotePhysicalTtlSecs,
                                     Map<String, String> metadata) {
        super(cacheName, key, localLogicTtlSecs, localPhysicalTtlSecs, remoteLogicTtlSecs,
                remotePhysicalTtlSecs, metadata);
        this.field = field;
        this.value = value;
    }


    @Override
    public boolean isBatch() {
        return false;
    }
}
