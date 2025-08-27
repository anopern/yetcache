package com.yetcache.core.cache.command.kv;

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

    public static KvCacheRemoveCommand of(final Object bizKey) {
        return KvCacheRemoveCommand.builder()
                .bizKey(bizKey)
                .build();
    }
}
