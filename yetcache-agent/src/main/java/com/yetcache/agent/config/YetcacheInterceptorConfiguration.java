package com.yetcache.agent.config;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.hash.batchget.HashCacheBatchGetInterceptor;
import com.yetcache.agent.core.structure.hash.get.HashCacheGetInterceptor;
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
    public HashCacheGetInterceptor dynamicHashCacheGetInterceptor(
            CacheInvocationInterceptorRegistry interceptorRegistry) {
        HashCacheGetInterceptor interceptor = new HashCacheGetInterceptor();
        interceptorRegistry.register(interceptor);
        return interceptor;
    }

    @Bean
    public HashCacheBatchGetInterceptor dynamicHashCacheBatchGetInterceptor(
            CacheInvocationInterceptorRegistry interceptorRegistry) {
        HashCacheBatchGetInterceptor interceptor = new HashCacheBatchGetInterceptor();
        interceptorRegistry.register(interceptor);
        return interceptor;
    }

    @Bean
    public ApplicationRunner registerDefaultChains(CacheInvocationChainRegistry registry,
                                                   CacheInvocationChainBuilder chainBuilder) {
        return args -> {
            StructureBehaviorKey dhGetSb = StructureBehaviorKey.of(StructureType.DYNAMIC_HASH, BehaviorType.GET);
            StructureBehaviorKey dhBatchGetSb = StructureBehaviorKey.of(StructureType.DYNAMIC_HASH, BehaviorType.BATCH_GET);
            CacheInvocationChain dhGetChain = chainBuilder.build(dhGetSb);
            CacheInvocationChain dhBatchGetChain = chainBuilder.build(dhBatchGetSb);
            registry.register(dhGetSb, dhGetChain);
            registry.register(dhBatchGetSb, dhBatchGetChain);
        };
    }
}
