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

    private BroadcastDelayTolerance delayTolerance;

    public BroadcastConfig(BroadcastConfig other) {
        if (other == null) {
            return;
        }
        this.type = other.type;
        this.rabbitmq = other.rabbitmq;
        this.delayTolerance = other.delayTolerance;
    }

    public static BroadcastConfig defaultConfig() {
        BroadcastConfig config = new BroadcastConfig();
        config.setType(MqType.RABBIT_MQ);
        config.setRabbitmq(RabbitMqConfig.defaultConfig());
        return config;
    }

}
