package com.yetcache.core.config.hash;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@NoArgsConstructor
public class RedisHashCacheConfig {
    protected Long logicTtlSecs;
    protected Long physicalTtlSecs;
    protected Double ttlRandomPct;

    public RedisHashCacheConfig(RedisHashCacheConfig other) {
        this.logicTtlSecs = other.logicTtlSecs;
        this.physicalTtlSecs = other.physicalTtlSecs;
        this.ttlRandomPct = other.ttlRandomPct;
    }
    public static RedisHashCacheConfig defaultConfig() {
        RedisHashCacheConfig config = new RedisHashCacheConfig();
        config.setLogicTtlSecs(20 * 60L);
        config.setPhysicalTtlSecs(3 * 24 * 3600L);
        config.setTtlRandomPct(0.15);
        return config;
    }
}
