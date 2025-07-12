package com.yetcache.agent;

import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.YetCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Configuration
@EnableConfigurationProperties(YetCacheProperties.class)
public class YetcacheAgentConfig {
    @Bean
    public YetCacheConfigResolver yetCacheConfigResolver(YetCacheProperties props) {
        return new YetCacheConfigResolver(props);
    }
}
