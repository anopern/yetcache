package com.yetcache.mq;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.properties.BaseCacheAgentProperties;
import com.yetcache.utils.CacheAgentMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/5/22
 */
@Configuration
@Slf4j
public class CacheAgentMQListenerConfig {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private CacheAgentMessageProcessor messageProcessor;
    @Autowired
    @Qualifier("cacheAgentAnonymousQueueMap")
    private Map<String, Queue> anonymousQueueMap;

    @PostConstruct
    public void registerListeners() {
        Map<String, BaseCacheAgentProperties> beans =
                applicationContext.getBeansOfType(BaseCacheAgentProperties.class);
        if (CollUtil.isEmpty(beans)) {
            log.warn("暂无需要处理的queue！");
        }

        Set<String> queueNames = anonymousQueueMap.values().stream()
                .map(Queue::getName)
                .collect(Collectors.toSet());

        for (String queueName : queueNames) {
            // 为每个队列单独创建监听容器
            SimpleMessageListenerContainer container = getSimpleMessageListenerContainer(queueName);
            container.start(); // 启动监听器
        }
    }

    private SimpleMessageListenerContainer getSimpleMessageListenerContainer(String queueName) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);

        // 每个 queue 使用独立线程进行监听
        container.setConcurrentConsumers(1); // 每个 queue 单独消费线程数
        container.setMaxConcurrentConsumers(1); // 可动态调整

        container.setMessageListener(message -> messageProcessor.processMessage(message));

        return container;
    }

}
