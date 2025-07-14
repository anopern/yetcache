package com.yetcache.core.config.dynamichash;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@NoArgsConstructor
public class CaffeineDynamicHashCacheConfig {
    protected Long ttlSecs;
    protected Double ttlRandomPct;
    protected Integer maxSize;

    public CaffeineDynamicHashCacheConfig(CaffeineDynamicHashCacheConfig other) {
        if (other == null) return;
        this.ttlSecs = other.ttlSecs;
        this.ttlRandomPct = other.ttlRandomPct;
        this.maxSize = other.maxSize;
    }

    public static CaffeineDynamicHashCacheConfig defaultConfig() {
        CaffeineDynamicHashCacheConfig defaultConfig = new CaffeineDynamicHashCacheConfig();
        defaultConfig.setTtlSecs(300L);
        defaultConfig.setTtlRandomPct(0.15);
        defaultConfig.setMaxSize(10000);
        return defaultConfig;
    }
}
