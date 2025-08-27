package com.yetcache.agent.core.structure.kv;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.AgentScope;
import com.yetcache.agent.core.port.KvCacheAgentPutPort;
import com.yetcache.agent.core.port.KvCacheAgentRemovePort;
import com.yetcache.core.cache.kv.MultiTierKvCache;
import com.yetcache.agent.core.structure.kv.loader.KvCacheLoader;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.config.kv.KvCacheConfig;
import com.yetcache.core.support.key.KeyConverter;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public class KvCacheAgentScope implements AgentScope {
    private final String cacheAgentName;
    private final MultiTierKvCache multiLevelCache;
    private final KvCacheConfig config;
    private final KvCacheLoader<?> cacheLoader;
    private final CacheBroadcastPublisher broadcastPublisher;
    private final KvCacheAgentRemovePort cacheRemovePort;
    private final KvCacheAgentPutPort cachePutPort;
    private final TypeDescriptor typeDescriptor;
    private final KeyConverter keyConverter;

    public KvCacheAgentScope(String cacheAgentName,
                             MultiTierKvCache multiLevelCache,
                             KvCacheConfig config,
                             KeyConverter keyConverter,
                             KvCacheLoader<?> cacheLoader,
                             CacheBroadcastPublisher broadcastPublisher,
                             KvCacheAgentRemovePort cacheRemovePort,
                             KvCacheAgentPutPort cachePutPort,
                             TypeDescriptor typeDescriptor) {
        this.cacheAgentName = cacheAgentName;
        this.multiLevelCache = multiLevelCache;
        this.config = config;
        this.keyConverter = keyConverter;
        this.cacheLoader = cacheLoader;
        this.broadcastPublisher = broadcastPublisher;
        this.cacheRemovePort = cacheRemovePort;
        this.cachePutPort = cachePutPort;
        this.typeDescriptor = typeDescriptor;
    }
}
