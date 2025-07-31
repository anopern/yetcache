package com.yetcache.agent.config;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.get.DynamicHashCacheGetInterceptor;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.result.Result;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
    public <C extends CacheInvocationContext, T, R extends Result<T>> CacheInvocationChainBuilder<C, T, R>
    cacheInvocationChainBuilder(CacheInvocationInterceptorRegistry<C, T, R> interceptorRegistry) {
        return new CacheInvocationChainBuilder<>(interceptorRegistry);
    }

    @Bean
    public CacheInvocationChainRegistry cacheInvocationChainRegistry() {
        return new CacheInvocationChainRegistry();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public <C extends CacheInvocationContext, T, R extends Result<T>> DynamicHashCacheGetInterceptor<C, T, R>
    dynamicHashCacheGetInterceptor(CacheInvocationInterceptorRegistry<C, T, R> interceptorRegistry) {
        DynamicHashCacheGetInterceptor<C, T, R> interceptor = new DynamicHashCacheGetInterceptor<>();
        interceptorRegistry.register((CacheInterceptor<C, T, R>) interceptor);
        return interceptor;
    }

    @Bean
    public <C extends CacheInvocationContext, T, R extends Result<T>> ApplicationRunner
    registerDefaultChains(CacheInvocationChainRegistry registry, CacheInvocationChainBuilder<C, T, R> chainBuilder) {
        return args -> {
            StructureBehaviorKey sbKey = StructureBehaviorKey.of(StructureType.DYNAMIC_HASH, BehaviorType.SINGLE_GET);
            CacheInvocationChain<C, T, R> chin = chainBuilder.build(sbKey);
            registry.register(sbKey, chin);
        };
    }
}
