package com.yetcache.agent.config;

import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.YetCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@Configuration
@EnableConfigurationProperties(YetCacheProperties.class)
public class YetcacheAgentConfiguration {
    @Bean
    public CacheAgentRegistryHub cacheAgentRegistryHub() {
        return new CacheAgentRegistryHub();
    }

    @Bean
    public YetCacheConfigResolver yetCacheConfigResolver(YetCacheProperties props) {
        return new YetCacheConfigResolver(props);
    }

}
