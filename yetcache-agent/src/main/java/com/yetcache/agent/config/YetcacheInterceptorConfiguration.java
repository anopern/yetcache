package com.yetcache.agent.config;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.get.DynamicHashCacheGetInterceptor;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.config.YetCacheProperties;
import org.springframework.boot.ApplicationRunner;
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
    public CacheInvocationInterceptorRegistry cacheInvocationInterceptorRegistry() {
        return new CacheInvocationInterceptorRegistry();
    }

    @Bean
    public CacheInvocationChainBuilder cacheInvocationChainBuilder(CacheInvocationInterceptorRegistry interceptorRegistry) {
        return new CacheInvocationChainBuilder(interceptorRegistry);
    }

    @Bean
    public CacheInvocationChainRegistry cacheInvocationChainRegistry() {
        return new CacheInvocationChainRegistry();
    }

    @Bean
    public DynamicHashCacheGetInterceptor dynamicHashCacheGetInterceptor(
            CacheInvocationInterceptorRegistry interceptorRegistry) {
        DynamicHashCacheGetInterceptor interceptor = new DynamicHashCacheGetInterceptor();
        interceptorRegistry.register(interceptor);
        return interceptor;
    }

    @Bean
    public ApplicationRunner registerDefaultChains(CacheInvocationChainRegistry registry,
                                                   CacheInvocationChainBuilder chainBuilder) {
        return args -> {
            StructureBehaviorKey sbKey = StructureBehaviorKey.of(StructureType.DYNAMIC_HASH, BehaviorType.SINGLE_GET);
            CacheInvocationChain chin = chainBuilder.build(sbKey);
            registry.register(sbKey, chin);
        };
    }
}
