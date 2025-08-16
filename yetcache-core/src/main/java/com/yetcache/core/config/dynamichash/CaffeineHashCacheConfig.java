package com.yetcache.core.config.dynamichash;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@NoArgsConstructor
public class CaffeineHashCacheConfig {
    protected Long logicTtlSecs;
    protected Long physicalTtlSecs;
    protected Double ttlRandomPct;
    protected Integer maxSize;

    public CaffeineHashCacheConfig(CaffeineHashCacheConfig other) {
        if (other == null) return;
        this.logicTtlSecs = other.logicTtlSecs;
        this.physicalTtlSecs = other.physicalTtlSecs;
        this.ttlRandomPct = other.ttlRandomPct;
        this.maxSize = other.maxSize;
    }

    public static CaffeineHashCacheConfig defaultConfig() {
        CaffeineHashCacheConfig defaultConfig = new CaffeineHashCacheConfig();
        defaultConfig.setLogicTtlSecs(300L);
        defaultConfig.setPhysicalTtlSecs(7 * 24 * 60 * 60L);
        defaultConfig.setTtlRandomPct(0.15);
        defaultConfig.setMaxSize(10000);
        return defaultConfig;
    }
}
