package com.yetcache.core.cache.command.hash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@AllArgsConstructor
@Data
@Builder
public class HashCacheBatchRemoveCommand {
    private final Object bizKey;
    private final List<Object> bizFields;

    public static HashCacheBatchRemoveCommand of(Object bizKey, List<Object> bizFields) {
        return new HashCacheBatchRemoveCommand(bizKey, bizFields);
    }
}
