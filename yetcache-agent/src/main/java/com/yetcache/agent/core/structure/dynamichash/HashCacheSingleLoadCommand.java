package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.result.Metadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HashCacheSingleLoadCommand {
    private Object bizKey;
    private Object bizField;
    private CacheAccessContext context;
    private Metadata metadata;
}
