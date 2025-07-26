package com.yetcache.agent.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yetcache.agent.broadcast.BroadcastQueueInitializer;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.agent.broadcast.sender.CacheBroadcastSenderBak;
import com.yetcache.agent.broadcast.sender.DefaultRabbitmqCacheBroadcastSenderBak;
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
public class YetcacheAgentConfiguration {
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
    public CacheBroadcastSenderBak cacheBroadcastSender(YetCacheProperties yetCacheProperties) {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();
        return new DefaultRabbitmqCacheBroadcastSenderBak(config, rabbitTemplate);
    }

//    @Bean
//    public CacheBroadcastReceiver cacheBroadcastReceiver(CacheAgentRegistryBak registry,
//                                                         CacheBroadcastHandlerRegistry handlerRegistry) {
//        return new DefaultCacheBroadcastReceiver(registry, handlerRegistry);
//    }
}
