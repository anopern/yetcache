package com.yetcache.example.cache.cnfig;

import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.TypeFieldConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import com.yetcache.example.cache.agent.StockHoldInfoCacheAgent;
import com.yetcache.example.entity.StockHoldInfo;
import com.yetcache.example.enums.EnumCaches;
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
    private RedissonClient redissonClient;
    @Autowired
    private DynamicHashCacheLoader stockHoldInfoCacheLoader;
    @Autowired
    private CacheInvocationChainRegistry cacheInvocationChainRegistry;

    @Bean
    public StockHoldInfoCacheAgent<StockHoldInfo> stockHoldInfoCacheAgent(YetCacheConfigResolver configResolver,
                                                                          CacheAgentRegistryHub agentRegistryHub) {
        String componentName = EnumCaches.STOCK_HOLD_INFO_CACHE.getName();
        DynamicHashCacheConfig config = configResolver.resolveDynamicHash(componentName);
        StockHoldInfoCacheAgent<StockHoldInfo> agent = new StockHoldInfoCacheAgent<>(componentName,
                config, redissonClient,
                KeyConverterFactory.createDefault(config.getSpec().getKeyPrefix(), config.getSpec().getUseHashTag()),
                new TypeFieldConverter(Long.class),
                stockHoldInfoCacheLoader,
                cacheInvocationChainRegistry);
        agentRegistryHub.register(agent);
        return agent;
    }
}
