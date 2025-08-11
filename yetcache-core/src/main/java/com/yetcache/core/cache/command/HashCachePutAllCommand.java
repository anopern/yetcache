package com.yetcache.core.cache.command;

import com.yetcache.core.cache.CacheTtl;
import com.yetcache.core.cache.WriteTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


/**
 * @author walter.yan
 * @since 2025/7/30
 */
@AllArgsConstructor
@Data
@Builder
public class HashCachePutAllCommand {
    private final Object bizKey;
    private final Map<Object, Object> valueMap;
    private final CacheTtl ttl;
    private final WriteTier writeTier;
}
