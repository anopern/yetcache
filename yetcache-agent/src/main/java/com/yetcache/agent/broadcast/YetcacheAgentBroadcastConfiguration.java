package com.yetcache.agent.broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yetcache.agent.broadcast.receiver.CacheBroadcastReceiver;
import com.yetcache.agent.broadcast.receiver.DefaultExecutableCommandDispatcher;
import com.yetcache.agent.broadcast.receiver.ExecutableCommandDispatcher;
import com.yetcache.agent.broadcast.receiver.RabbitMqCacheBroadcastReceiver;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.agent.broadcast.receiver.handler.DynamicHashPutAllHandler;
import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.broadcast.publisher.DefaultRabbitmqCacheBroadcastPublisher;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.broadcast.RabbitMqConfig;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
public class YetcacheAgentBroadcastConfiguration {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public YetCacheConfigResolver yetCacheConfigResolver(YetCacheProperties props) {
        return new YetCacheConfigResolver(props);
    }

    @Bean
    public String broadcastQueueName(YetCacheProperties yetCacheProperties, ConnectionFactory springConnectionFactory) throws IOException {
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
    public CacheBroadcastPublisher cacheBroadcastSender(YetCacheProperties yetCacheProperties) {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();
        return new DefaultRabbitmqCacheBroadcastPublisher(config, rabbitTemplate);
    }

    @Bean
    public ExecutableCommandDispatcher executableCommandDispatcher(CacheBroadcastHandlerRegistry handlerRegistry) {
        return new DefaultExecutableCommandDispatcher(handlerRegistry);
    }

    @Bean
    public CacheBroadcastReceiver cacheBroadcastReceiver(ExecutableCommandDispatcher executableCommandDispatcher) {
        return new RabbitMqCacheBroadcastReceiver(OBJECT_MAPPER, executableCommandDispatcher);
    }

    @Bean
    public CacheAgentRegistryHub cacheAgentRegistryHub() {
        return new CacheAgentRegistryHub();
    }

    @Bean
    public DynamicHashPutAllHandler<?, ?, ?> dynamicHashPutAllHandler(CacheAgentRegistryHub cacheAgentRegistryHub,
                                                                      CacheBroadcastHandlerRegistry handlerRegistry) {
        DynamicHashPutAllHandler<?, ?, ?> handler = new DynamicHashPutAllHandler<>(cacheAgentRegistryHub);
        handlerRegistry.register(handler);
        return handler;
    }
}
