package com.yetcache.agent.core.structure.hash;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.AgentScope;
import com.yetcache.agent.core.port.HashCacheFillPort;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.cache.hash.MultiLevelHashCache;
import com.yetcache.core.config.hash.HashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public class HashAgentScope implements AgentScope {
    private final String componentName;
    private final MultiLevelHashCache multiLevelCache;
    private final HashCacheConfig config;
    private final HashCacheLoader cacheLoader;
    private final CacheBroadcastPublisher broadcastPublisher;
    private final HashCacheFillPort hashCacheFillPort;
    private final TypeDescriptor typeDescriptor;
    private final KeyConverter keyConverter;
    private final FieldConverter fieldConverter;

    public HashAgentScope(String componentName,
                          MultiLevelHashCache multiLevelCache,
                          HashCacheConfig config,
                          KeyConverter keyConverter,
                          FieldConverter fieldConverter,
                          HashCacheLoader cacheLoader,
                          CacheBroadcastPublisher broadcastPublisher,
                          HashCacheFillPort hashCacheFillPort,
                          TypeDescriptor typeDescriptor) {
        this.componentName = componentName;
        this.multiLevelCache = multiLevelCache;
        this.config = config;
        this.keyConverter = keyConverter;
        this.fieldConverter = fieldConverter;
        this.cacheLoader = cacheLoader;
        this.broadcastPublisher = broadcastPublisher;
        this.hashCacheFillPort = hashCacheFillPort;
        this.typeDescriptor = typeDescriptor;
    }
}
