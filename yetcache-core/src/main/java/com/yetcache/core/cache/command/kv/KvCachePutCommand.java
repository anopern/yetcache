package com.yetcache.core.cache.command.kv;

import com.yetcache.core.cache.CacheTtl;
import com.yetcache.core.cache.WriteLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;


/**
 * @author walter.yan
 * @since 2025/7/30
 */
@AllArgsConstructor
@Data
@Builder
public class KvCachePutCommand {
    private final Object bizKey;
    private final Object value;
    private final CacheTtl ttl;

    public static   KvCachePutCommand of (final Object bizKey, final Object value, final CacheTtl ttl) {
        return new KvCachePutCommand(bizKey, value, ttl);
    }
}
