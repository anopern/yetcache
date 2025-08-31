package com.yetcache.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.agent.CacheAgentPortRegistry;
import com.yetcache.agent.broadcast.publisher.RedisInvalidateMessagePublisher;
import com.yetcache.agent.broadcast.subscriber.CacheInvalidateMessageSubscriber;
import com.yetcache.agent.broadcast.subscriber.RedisCacheInvalidateMessageSubscriber;
import com.yetcache.core.config.RedisPubSubConfig;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@EnableConfigurationProperties(YetCacheProperties.class)
@Configuration
public class YetcachePubSubAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RedisInvalidateMessagePublisher redisInvalidateMessagePublisher(
            YetCacheProperties props,
            RedissonClient redissonClient,
            @Qualifier("yetcacheCodecObjectMapper") ObjectMapper objectMapper) {
        RedisPubSubConfig config = props.getPubSub();
        return new RedisInvalidateMessagePublisher(config, redissonClient, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheInvalidateMessageSubscriber redisCacheBroadcastPublisher(
            YetCacheProperties props,
            RedissonClient redissonClient,
            @Qualifier("yetcacheCodecObjectMapper") ObjectMapper objectMapper,
            CacheAgentPortRegistry agentPortRegistry) {
        RedisPubSubConfig config = props.getPubSub();
        return new RedisCacheInvalidateMessageSubscriber(config, redissonClient, objectMapper, agentPortRegistry);
    }
}

















