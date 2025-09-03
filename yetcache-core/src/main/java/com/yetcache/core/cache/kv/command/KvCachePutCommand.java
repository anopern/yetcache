package com.yetcache.core.cache.kv.command;

import com.yetcache.core.cache.CacheTtl;
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
public class KvCachePutCommand {
    private final String key;
    private final Object value;
    private final CacheTtl ttl;

    public static KvCachePutCommand of(final String key, final Object value, final CacheTtl ttl) {
        return new KvCachePutCommand(key, value, ttl);
    }
}
