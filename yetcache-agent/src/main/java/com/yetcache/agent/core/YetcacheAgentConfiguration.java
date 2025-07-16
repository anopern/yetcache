package com.yetcache.agent.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yetcache.agent.broadcast.BroadcastQueueInitializer;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.broadcast.RabbitMqConfig;
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
    @Bean
    public YetCacheConfigResolver yetCacheConfigResolver(YetCacheProperties props) {
        return new YetCacheConfigResolver(props);
    }

    @Bean
    public String broadcastQueueName(YetCacheProperties yetCacheProperties, Connection connection) throws IOException {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();

        // 创建 Channel 并声明交换机和队列
        Channel channel = connection.createChannel();
        String queueName = BroadcastQueueInitializer.init(channel, config);

        // 可选：注册该 channel 或 queueName 到某个 registry（供接收端用）
        // BroadcastRegistry.register(queueName, channel);

        return queueName;
    }

}
