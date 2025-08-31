package com.yetcache.starter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.agent.*;
import com.yetcache.agent.agent.kv.interceptor.KvCacheGetInterceptor;
import com.yetcache.agent.agent.kv.interceptor.KvCachePutInterceptor;
import com.yetcache.agent.governance.plugin.MetricsInterceptor;
import com.yetcache.agent.interceptor.CacheInvocationChain;
import com.yetcache.agent.interceptor.CacheInvocationChainBuilder;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.agent.interceptor.CacheInvocationInterceptorRegistry;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.codec.jackson.JacksonJsonValueCodec;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author walter.yan
 * @since 2025/8/31
 */
@Configuration
public class YetCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheAgentRegistryHub cacheAgentRegistryHub() {
        return new CacheAgentRegistryHub();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheAgentPortRegistry cacheAgentPortRegistry() {
        return new CacheAgentPortRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public YetCacheConfigResolver yetCacheConfigResolver(YetCacheProperties props) {
        return new YetCacheConfigResolver(props);
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeRefRegistry typeRefRegistry() {
        return new TypeRefRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheInvocationInterceptorRegistry cacheInvocationInterceptorRegistry() {
        return new CacheInvocationInterceptorRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheInvocationChainBuilder cacheInvocationChainBuilder(CacheInvocationInterceptorRegistry interceptorRegistry) {
        return new CacheInvocationChainBuilder(interceptorRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheInvocationChainRegistry cacheInvocationChainRegistry() {
        return new CacheInvocationChainRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public MetricsInterceptor metricsInterceptor(
            CacheInvocationInterceptorRegistry interceptorRegistry,
            MeterRegistry registry) {
        MetricsInterceptor interceptor = new MetricsInterceptor(registry);
        interceptorRegistry.register(interceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public KvCacheGetInterceptor kvCacheGetInterceptor(
            CacheInvocationInterceptorRegistry interceptorRegistry) {
        KvCacheGetInterceptor interceptor = new KvCacheGetInterceptor();
        interceptorRegistry.register(interceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public KvCachePutInterceptor kvCachePutInterceptor(
            CacheInvocationInterceptorRegistry interceptorRegistry) {
        KvCachePutInterceptor interceptor = new KvCachePutInterceptor();
        interceptorRegistry.register(interceptor);
        return interceptor;
    }

    @Qualifier("yetcacheCodecObjectMapper")
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper yetcacheCodecObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    @ConditionalOnMissingBean
    public JsonValueCodec jsonValueCodec(@Qualifier("yetcacheCodecObjectMapper") ObjectMapper objectMapper) {
        return new JacksonJsonValueCodec(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationRunner registerDefaultChains(
            CacheAgentRegistryHub agentRegistryHub,
            CacheInvocationChainRegistry registry,
            CacheInvocationChainBuilder chainBuilder) {
        return args -> {
            for (CacheAgent agent : agentRegistryHub.allKvAgents()) {
                ChainKey kvGetChainKey = ChainKey.of(StructureType.KV, BehaviorType.GET, agent.cacheAgentName());
                CacheInvocationChain kvGetChain = chainBuilder.build(kvGetChainKey);
                registry.register(kvGetChainKey, kvGetChain);
            }
        };
    }
}
