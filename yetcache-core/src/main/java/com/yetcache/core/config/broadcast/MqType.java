package com.yetcache.core.config.broadcast;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public enum MqType {
    RABBIT_MQ("rabbitmq"),
    ;

    private final String type;

    MqType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @JsonCreator
    public static MqType from(String input) {
        if (input == null) {
            return null;
        }
        for (MqType mqType : values()) {
            if (mqType.type.equalsIgnoreCase(input.trim())) {
                return mqType;
            }
        }
        throw new IllegalArgumentException("Invalid MqType: " + input);
    }
}
