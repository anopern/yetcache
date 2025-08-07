package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.agent.core.AgentScope;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public class DynamicHashAgentScope implements AgentScope {
    private final String componentName;
    private final MultiTierDynamicHashCache multiTierCache;
    private final DynamicHashCacheConfig config;
    private final DynamicHashCacheLoader cacheLoader;

    public DynamicHashAgentScope(String componentName,
                                 MultiTierDynamicHashCache multiTierCache,
                                 DynamicHashCacheConfig config,
                                 DynamicHashCacheLoader cacheLoader  ) {
        this.componentName = componentName;
        this.multiTierCache = multiTierCache;
        this.config = config;
        this.cacheLoader = cacheLoader;
    }
}
