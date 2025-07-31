package com.yetcache.agent.builder;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.structure.dynamichash.BaseDynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
public class PlatformAgentBuilder {

    private final CacheInvocationChainRegistry chainRegistry;

    public PlatformAgentBuilder(CacheInvocationChainRegistry chainRegistry) {
        this.chainRegistry = chainRegistry;
    }

    public <K, F, V> DynamicHashCacheAgent<K, F, V> buildAgent(String componentNane,
                                                               DynamicHashCacheConfig config,
                                                               RedissonClient redissonClient,
                                                               KeyConverter<K> keyConverter,
                                                               FieldConverter<F> fieldConverter,
                                                               DynamicHashCacheLoader<K, F, V> cacheLoader,
                                                               CacheInvocationChainRegistry chainRegistry,
                                                               CacheBroadcastPublisher broadcastPublisher) {
        return new BaseDynamicHashCacheAgent<>(
                componentNane,
                config,
                redissonClient,
                keyConverter,
                fieldConverter,
                cacheLoader,
                chainRegistry,
                broadcastPublisher
        );

    }
}
