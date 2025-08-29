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
    private final Object bizKey;
    private final CacheLevel cacheLevel;

    public static KvCacheRemoveCommand of(final Object bizKey) {
        return KvCacheRemoveCommand.builder()
                .bizKey(bizKey)
                .build();
    }

    public static KvCacheRemoveCommand ofLocal(final Object bizKey) {
        return KvCacheRemoveCommand.builder()
                .bizKey(bizKey)
                .cacheLevel(CacheLevel.LOCAL)
                .build();
    }
}
