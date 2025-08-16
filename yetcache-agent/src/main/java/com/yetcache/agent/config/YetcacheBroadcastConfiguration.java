package com.yetcache.agent.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yetcache.agent.broadcast.BroadcastQueueInitializer;
import com.yetcache.agent.broadcast.receiver.CacheBroadcastReceiver;
import com.yetcache.agent.broadcast.receiver.RabbitMqCacheBroadcastReceiver;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.broadcast.publisher.DefaultRabbitmqCacheBroadcastPublisher;
import com.yetcache.agent.broadcast.receiver.handler.HashCacheAgentPutAllHandler;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.codec.JsonTypeConverter;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.broadcast.MessageDelayPolicyRegistry;
import com.yetcache.core.config.broadcast.RabbitMqConfig;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Configuration
@EnableConfigurationProperties(YetCacheProperties.class)
public class YetcacheBroadcastConfiguration {

    @Bean
    public String broadcastQueueName(YetCacheProperties yetCacheProperties,
                                     ConnectionFactory springConnectionFactory) throws IOException {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();

        // 获取底层 RabbitMQ Connection
        Connection rabbitConnection = springConnectionFactory.createConnection().getDelegate();
        assert rabbitConnection != null;
        Channel channel = rabbitConnection.createChannel();
        return BroadcastQueueInitializer.init(channel, config);
    }

    @Bean
    public CacheBroadcastHandlerRegistry cacheBroadcastHandlerRegistry() {
        return new CacheBroadcastHandlerRegistry();
    }

    @Bean
    public CacheBroadcastPublisher cacheBroadcastSender(RabbitTemplate rabbitTemplate,
                                                        YetCacheProperties yetCacheProperties) {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();
        return new DefaultRabbitmqCacheBroadcastPublisher(config, rabbitTemplate);
    }

    @Bean
    public CacheBroadcastReceiver cacheBroadcastReceiver(
            JsonValueCodec jsonValueCodec,
            JsonTypeConverter jsonTypeConverter,
            CacheBroadcastHandlerRegistry handlerRegistry,
            MessageDelayPolicyRegistry delayPolicyRegistry) {
        return new RabbitMqCacheBroadcastReceiver(jsonValueCodec, jsonTypeConverter, handlerRegistry, delayPolicyRegistry);
    }

    @Bean
    public HashCacheAgentPutAllHandler hashCacheAgentPutAllHandler(CacheAgentRegistryHub cacheAgentRegistryHub,
                                                                   CacheBroadcastHandlerRegistry handlerRegistry,
                                                                   TypeRefRegistry typeRefRegistry,
                                                                   JsonTypeConverter jsonTypeConverter) {
        HashCacheAgentPutAllHandler handler = new HashCacheAgentPutAllHandler(cacheAgentRegistryHub,
                typeRefRegistry, jsonTypeConverter);
        handlerRegistry.register(handler);
        return handler;
    }
}
