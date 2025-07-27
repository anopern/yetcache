package com.yetcache.example.cache.cnfig;

import com.yetcache.agent.broadcast.sender.CacheBroadcastPublisher;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.TypeFieldConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import com.yetcache.example.cache.agent.StockHoldInfoCacheAgent;
import com.yetcache.example.enums.EnumCaches;
import com.yetcache.example.service.loader.StockHoldInfoCacheLoader;
import io.micrometer.core.instrument.MeterRegistry;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
@Configuration
public class DynamicHashCacheAgentConfig {
    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private YetCacheConfigResolver configResolver;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StockHoldInfoCacheLoader stockHoldInfoCacheLoader;
    @Autowired
    private CacheBroadcastPublisher cacheBroadcastPublisher;

    @Bean
    public StockHoldInfoCacheAgent stockHoldInfoCacheAgent(CacheAgentRegistryHub agentRegistryHub) {
        String componentName = EnumCaches.STOCK_HOLD_INFO_CACHE.getName();
        DynamicHashCacheConfig config = configResolver.resolveDynamicHash(componentName);
        StockHoldInfoCacheAgent agent = new StockHoldInfoCacheAgent(componentName,
                config, redissonClient,
                KeyConverterFactory.createDefault(config.getSpec().getKeyPrefix(), config.getSpec().getUseHashTag()),
                new TypeFieldConverter<>(Long.class),
                stockHoldInfoCacheLoader,
                null,
                cacheBroadcastPublisher);
        agentRegistryHub.register(agent);
        return agent;
    }
}
