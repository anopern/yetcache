package com.yetcache.example.cache.cnfig;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.structure.dynamichash.BaseDynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.codec.jackson.JacksonJsonValueCodec;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.dynamichash.HashCacheConfig;
import com.yetcache.core.support.field.TypeFieldConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import com.yetcache.example.entity.StockHoldInfo;
import com.yetcache.example.enums.EnumCaches;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
@Configuration
public class HashCacheAgentConfig {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Bean
    public JacksonJsonValueCodec objectMapperCodec() {
        return new JacksonJsonValueCodec(objectMapper);
    }

    @Qualifier("stockHoldInfoCacheAgent")
    @Bean
    public BaseDynamicHashCacheAgent stockHoldInfoCacheAgent(
            RedissonClient redissonClient,
            YetCacheConfigResolver configResolver,
            CacheAgentRegistryHub agentRegistryHub,
            DynamicHashCacheLoader stockHoldInfoCacheLoader,
            CacheInvocationChainRegistry cacheInvocationChainRegistry,
            CacheBroadcastPublisher broadcastPublisher,
            JacksonJsonValueCodec jacksonValueCodec) {
        String componentName = EnumCaches.STOCK_HOLD_INFO_CACHE.getName();
        HashCacheConfig config = configResolver.resolveHash(componentName);
        BaseDynamicHashCacheAgent agent = new BaseDynamicHashCacheAgent(componentName,
                config, redissonClient,
                KeyConverterFactory.createDefault(config.getSpec().getKeyPrefix(), config.getSpec().getUseHashTag()),
                new TypeFieldConverter(Long.class),
                stockHoldInfoCacheLoader,
                broadcastPublisher,
                cacheInvocationChainRegistry,
                new TypeDescriptor(new TypeRef<StockHoldInfo>() {
                }),
                jacksonValueCodec);
        agentRegistryHub.register(agent);
        return agent;
    }
}
