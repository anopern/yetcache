package com.yetcache.core.cache.command;

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

    public static HashCacheRemoveCommand of(final Object bizKey, final Object bizField) {
        return HashCacheRemoveCommand.builder()
                .bizKey(bizKey)
                .bizField(bizField)
                .build();
    }
}
