package com.yetcache.example.cache.cnfig;

import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.flathash.FlatHashCacheConfig;
import com.yetcache.example.cache.agent.ConfigCommonInfoCacheAgent;
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
    private ConfigCommonInfoCacheLoader configCommonInfoCacheLoader;

    @Bean
    public ConfigCommonInfoCacheAgent configCommonInfoCacheAgent() {
        String cacheName = EnumCaches.CONFIG_COMMON_INFO_CACHE.getName();
        FlatHashCacheConfig config = configResolver.resolveFlatHash(cacheName);
        return new ConfigCommonInfoCacheAgent(cacheName, config, configCommonInfoCacheLoader, meterRegistry);
    }
}
