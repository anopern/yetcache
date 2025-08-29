package com.yetcache.core.config.broadcast;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Data
@NoArgsConstructor
public class BroadcastConfig {
    private MqType type;
    private RabbitMqConfig rabbitmq;

    public BroadcastConfig(BroadcastConfig other) {
        if (other == null) {
            return;
        }
        this.type = other.type;
        this.rabbitmq = other.rabbitmq;
    }

    public static BroadcastConfig defaultConfig() {
        BroadcastConfig config = new BroadcastConfig();
        config.setType(MqType.RABBIT_MQ);
        config.setRabbitmq(RabbitMqConfig.defaultConfig());
        return config;
    }

}
