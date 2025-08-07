package com.yetcache.example.cache.agent;

import com.yetcache.agent.core.structure.dynamichash.BaseDynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public class StockHoldInfoCacheAgent<V> extends BaseDynamicHashCacheAgent<V> {
    public StockHoldInfoCacheAgent(String componentNane,
                                   DynamicHashCacheConfig config,
                                   RedissonClient redissonClient,
                                   KeyConverter keyConverter,
                                   FieldConverter fieldConverter,
                                   DynamicHashCacheLoader cacheLoader,
                                   CacheInvocationChainRegistry chainRegistry
    ) {
        super(componentNane, config, redissonClient, keyConverter, fieldConverter, cacheLoader, chainRegistry);
    }
}
