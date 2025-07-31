package com.yetcache.agent.config;

import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
import com.yetcache.agent.core.structure.dynamichash.get.DynamicHashCacheGetInterceptor;
import com.yetcache.agent.interceptor.InvocationChainRegistry;
import com.yetcache.core.config.YetCacheProperties;
import org.checkerframework.checker.units.qual.K;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Configuration
@EnableConfigurationProperties(YetCacheProperties.class)
public class YetcacheInterceptorConfiguration {

    @Bean
    public <K, F, V> DynamicHashCacheGetInterceptor<K, F, V> dynamicHashCacheGetInterceptor(DynamicHashAgentScope dynamicHashAgentScope) {
        return new DynamicHashCacheGetInterceptor<>(dynamicHashAgentScope);
    }

    @Bean
    public InvocationChainRegistry invocationChainRegistry() {
        return new InvocationChainRegistry();
    }
}
