package com.yetcache.example.cache.cnfig;

import com.yetcache.agent.regitry.CacheAgentRegistry;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.example.cache.ConfigCommonInfoCacheAgent;
import com.yetcache.example.enums.EnumCaches;
import com.yetcache.example.service.loader.ConfigCommonInfoCacheLoader;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Configuration
public class ConfigCacheAgentConfig {
    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private YetCacheConfigResolver configResolver;
    @Autowired
    private CacheAgentRegistry cacheAgentRegistry;

    @Autowired
    private ConfigCommonInfoCacheLoader configCommonInfoCacheLoader;

    @Bean
    public ConfigCommonInfoCacheAgent configCommonInfoCacheAgent() {
        MultiTierFlatHashCacheConfig config = configResolver.resolveFlatHash(EnumCaches.CONFIG_COMMON_INFO_CACHE.getName());
        ConfigCommonInfoCacheAgent agent = new ConfigCommonInfoCacheAgent(config, configCommonInfoCacheLoader,
                meterRegistry);
        cacheAgentRegistry.register(agent);
        return agent;
    }
}
