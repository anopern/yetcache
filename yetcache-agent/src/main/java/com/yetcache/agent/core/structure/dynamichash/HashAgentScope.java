package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.AgentScope;
import com.yetcache.agent.core.port.HashCacheFillPort;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.cache.hash.MultiTierHashCache;
import com.yetcache.core.config.dynamichash.HashCacheConfig;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public class HashAgentScope implements AgentScope {
    private final String componentName;
    private final MultiTierHashCache multiTierCache;
    private final HashCacheConfig config;
    private final HashCacheLoader cacheLoader;
    private final CacheBroadcastPublisher broadcastPublisher;
    private final HashCacheFillPort hashCacheFillPort;
    private final TypeDescriptor typeDescriptor;

    public HashAgentScope(String componentName,
                          MultiTierHashCache multiTierCache,
                          HashCacheConfig config,
                          HashCacheLoader cacheLoader,
                          CacheBroadcastPublisher broadcastPublisher,
                          HashCacheFillPort hashCacheFillPort,
                          TypeDescriptor typeDescriptor) {
        this.componentName = componentName;
        this.multiTierCache = multiTierCache;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.broadcastPublisher = broadcastPublisher;
        this.hashCacheFillPort = hashCacheFillPort;
        this.typeDescriptor = typeDescriptor;
    }
}
