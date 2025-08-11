package com.yetcache.example.cache.cnfig;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.structure.dynamichash.BaseDynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.TypeFieldConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import com.yetcache.example.enums.EnumCaches;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
@Configuration
public class DynamicHashCacheAgentConfig {
    @Qualifier("stockHoldInfoCacheAgent")
    @Bean
    public BaseDynamicHashCacheAgent stockHoldInfoCacheAgent(
            RedissonClient redissonClient,
            YetCacheConfigResolver configResolver,
            CacheAgentRegistryHub agentRegistryHub,
            DynamicHashCacheLoader stockHoldInfoCacheLoader,
            CacheInvocationChainRegistry cacheInvocationChainRegistry,
            CacheBroadcastPublisher broadcastPublisher) {
        String componentName = EnumCaches.STOCK_HOLD_INFO_CACHE.getName();
        DynamicHashCacheConfig config = configResolver.resolveDynamicHash(componentName);
        BaseDynamicHashCacheAgent agent = new BaseDynamicHashCacheAgent(componentName,
                config, redissonClient,
                KeyConverterFactory.createDefault(config.getSpec().getKeyPrefix(), config.getSpec().getUseHashTag()),
                new TypeFieldConverter(Long.class),
                stockHoldInfoCacheLoader,
                broadcastPublisher,
                cacheInvocationChainRegistry);
        agentRegistryHub.register(agent);
        return agent;
    }
}
