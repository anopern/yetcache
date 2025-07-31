package com.yetcache.example.cache.agent;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.structure.dynamichash.BaseDynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.example.entity.StockHoldInfo;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public class StockHoldInfoCacheAgent extends BaseDynamicHashCacheAgent<String, Long, StockHoldInfo> {
    public StockHoldInfoCacheAgent(String componentNane,
                                   DynamicHashCacheConfig config,
                                   RedissonClient redissonClient,
                                   KeyConverter<String> keyConverter,
                                   FieldConverter<Long> fieldConverter,
                                   DynamicHashCacheLoader<String, Long, StockHoldInfo> cacheLoader,
                                   CacheInvocationChainRegistry chainRegistry,
                                   CacheBroadcastPublisher broadcastPublisher) {
        super(componentNane, config, redissonClient, keyConverter, fieldConverter, cacheLoader, chainRegistry,
                broadcastPublisher);
    }
}
