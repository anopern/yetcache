package com.yetcache.agent.broadcast.publisher;

import com.yetcache.agent.broadcast.command.CacheUpdateCommand;
import com.yetcache.core.config.broadcast.RabbitMqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author walter.yan
 * @since 2025/7/23
 */
public class DefaultRabbitmqCacheBroadcastPublisher implements CacheBroadcastPublisher {
    private final RabbitMqConfig config;
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    public DefaultRabbitmqCacheBroadcastPublisher(RabbitMqConfig config, RabbitTemplate rabbitTemplate) {
        this.config = config;
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = new Jackson2JsonMessageConverter();
    }

    @Override
    public void publish(CacheUpdateCommand command) {
        try {
            Message message = messageConverter.toMessage(command, new MessageProperties());
            message.getMessageProperties().setDeliveryMode(
                    config.getDurable() != null && config.getDurable()
                            ? MessageDeliveryMode.PERSISTENT
                            : MessageDeliveryMode.NON_PERSISTENT
            );
            rabbitTemplate.send(config.getExchange(), "", message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to broadcast cache command: " + command, e);
        }
    }
}
