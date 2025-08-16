package com.yetcache.agent.broadcast.command;

import lombok.Getter;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
public enum CacheShape {
    KV("kv"),
    HASH("hash"),
    ;

    @Getter
    private final String name;

    CacheShape(String name) {
        this.name = name;
    }

    public static Optional<CacheShape> fromName(String name) {
        for (CacheShape shape : CacheShape.values()) {
            if (shape.name.equals(name)) {
                return Optional.of(shape);
            }
        }
        return Optional.empty();
    }
}
