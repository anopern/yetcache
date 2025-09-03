package com.yetcache.core.cache.kv.command;

import com.yetcache.core.config.CacheLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@AllArgsConstructor
@Data
@Builder
public class KvCacheRemoveCommand {
    private final String key;
    private final CacheLevel cacheLevel;

    public static KvCacheRemoveCommand of(final String bizKey, final CacheLevel cacheLevel) {
        return KvCacheRemoveCommand.builder()
                .key(bizKey)
                .cacheLevel(cacheLevel)
                .build();
    }

    public static KvCacheRemoveCommand ofLocal(final String key) {
        return KvCacheRemoveCommand.builder()
                .key(key)
                .cacheLevel(CacheLevel.LOCAL)
                .build();
    }
}
