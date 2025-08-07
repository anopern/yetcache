package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.core.context.CacheAccessContext;
import lombok.AllArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@AllArgsConstructor
public class HashLoadCommand {
    private Object bizKey;
    private Object bizField;
    private CacheAccessContext context;
}
