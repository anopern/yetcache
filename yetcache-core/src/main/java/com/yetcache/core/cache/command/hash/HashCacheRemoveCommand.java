package com.yetcache.core.cache.command.hash;

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
public class HashCacheRemoveCommand {
    private final Object bizKey;
    private final Object bizField;
    private final CacheLevel cacheLevel;

    public static HashCacheRemoveCommand of(final Object bizKey, final Object bizField) {
        return HashCacheRemoveCommand.builder()
                .bizKey(bizKey)
                .bizField(bizField)
                .build();
    }
}
