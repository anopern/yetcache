package com.yetcache.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisPubSubConfig {
    private String topic;

    public RedisPubSubConfig(RedisPubSubConfig other) {
        if (null == other) {
            return;
        }
        this.topic = other.topic;
    }

    public static RedisPubSubConfig defaultConfig() {
        return new RedisPubSubConfig("yetcache:invalidate");
    }
}
