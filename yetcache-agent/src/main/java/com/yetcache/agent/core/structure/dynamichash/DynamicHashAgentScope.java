package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.AgentScope;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public class DynamicHashAgentScope<K, F, V> implements AgentScope {
    private final String componentName;
    private final MultiTierDynamicHashCache<K, F, V> multiTierCache;
    private final DynamicHashCacheConfig config;
    private final DynamicHashCacheLoader<K, F, V> cacheLoader;
    private final CacheBroadcastPublisher broadcastPublisher;

    public DynamicHashAgentScope(String componentName,
                                 MultiTierDynamicHashCache<K, F, V> multiTierCache,
                                 DynamicHashCacheConfig config,
                                 DynamicHashCacheLoader<K, F, V> cacheLoader,
                                 CacheBroadcastPublisher broadcastPublisher) {
        this.componentName = componentName;
        this.multiTierCache = multiTierCache;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.broadcastPublisher = broadcastPublisher;
    }
}
