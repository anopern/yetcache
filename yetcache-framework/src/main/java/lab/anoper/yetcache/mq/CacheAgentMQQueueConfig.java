package com.yxzq.common.cache.mq;

import com.yxzq.common.cache.properties.BaseCacheAgentProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/5/22
 */
@Configuration
@Slf4j
public class CacheAgentMQQueueConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 把 exchange -> 匿名 queue 的映射暴露出来供监听器使用
     */
    @Bean("cacheAgentAnonymousQueueMap")
    public Map<String, Queue> anonymousQueueMap() {
        Map<String, Queue> queueMap = new HashMap<>();

        Map<String, BaseCacheAgentProperties> beans =
                applicationContext.getBeansOfType(BaseCacheAgentProperties.class);

        for (BaseCacheAgentProperties props : beans.values()) {
            String exchange = props.getMqExchange();

            FanoutExchange fanoutExchange = new FanoutExchange(exchange, true, false);
            Queue q = new AnonymousQueue();

            amqpAdmin.declareExchange(fanoutExchange);
            amqpAdmin.declareQueue(q);
            amqpAdmin.declareBinding(BindingBuilder.bind(q).to(fanoutExchange));

            queueMap.put(exchange, q);
            log.info("已绑定匿名队列 [{}] 到广播交换机 [{}]", q.getName(), exchange);
        }

        return queueMap;
    }
}
