package com.yetcache.core.config.broadcast;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Data
@NoArgsConstructor
public class RabbitMqConfig {
    private String exchange;
    private String queuePrefix;
    private Boolean durable;
    private Boolean autoDelete;
    private Boolean anonymous;
    private Long queueExpireMs;

    public RabbitMqConfig(RabbitMqConfig other) {
        if (other == null) {
            return;
        }
        this.exchange = other.exchange;
        this.queuePrefix = other.queuePrefix;
        this.durable = other.durable;
        this.autoDelete = other.autoDelete;
        this.anonymous = other.anonymous;
        this.queueExpireMs = other.queueExpireMs;
    }

    public static RabbitMqConfig defaultConfig() {
        RabbitMqConfig config = new RabbitMqConfig();
        config.setExchange("yetcache.broadcast");
        config.setQueuePrefix("yetcache.sync.");
        config.setDurable(true);
        config.setAutoDelete(false);
        config.setAnonymous(false);
        config.setQueueExpireMs(24 * 3600 * 1000L);
        return config;
    }
}
