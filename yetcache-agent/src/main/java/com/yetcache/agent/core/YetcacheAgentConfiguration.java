package com.yetcache.agent.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yetcache.agent.broadcast.BroadcastQueueInitializer;
import com.yetcache.agent.broadcast.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.agent.broadcast.receiver.CacheBroadcastReceiver;
import com.yetcache.agent.broadcast.receiver.DefaultCacheBroadcastReceiver;
import com.yetcache.agent.broadcast.sender.CacheBroadcastSender;
import com.yetcache.agent.broadcast.sender.DefaultRabbitmqCacheBroadcastSender;
import com.yetcache.agent.regitry.CacheAgentRegistry;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.broadcast.RabbitMqConfig;
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
    public String broadcastQueueName(YetCacheProperties yetCacheProperties, Connection connection) throws IOException {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();

        // 创建 Channel 并声明交换机和队列
        Channel channel = connection.createChannel();

        return BroadcastQueueInitializer.init(channel, config);
    }

    @Bean
    public CacheBroadcastHandlerRegistry cacheBroadcastHandlerRegistry() {
        return new CacheBroadcastHandlerRegistry();
    }

    @Bean
    public CacheBroadcastSender cacheBroadcastSender(YetCacheProperties yetCacheProperties) {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();
        return new DefaultRabbitmqCacheBroadcastSender(config, rabbitTemplate);
    }

    @Bean
    public CacheBroadcastReceiver cacheBroadcastReceiver(CacheAgentRegistry registry,
                                                         CacheBroadcastHandlerRegistry handlerRegistry) {
        return new DefaultCacheBroadcastReceiver(registry, handlerRegistry);
    }
}
