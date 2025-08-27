package com.yetcache.core.config.kv;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@NoArgsConstructor
public class CaffeineKVCacheConfig {
    protected Long logicTtlSecs;
    protected Long physicalTtlSecs;
    protected Double ttlRandomPct;
    protected Integer maxSize;

    public CaffeineKVCacheConfig(CaffeineKVCacheConfig other) {
        if (other == null) {
            return;
        }
        this.logicTtlSecs = other.logicTtlSecs;
        this.physicalTtlSecs = other.physicalTtlSecs;
        this.ttlRandomPct = other.ttlRandomPct;
        this.maxSize = other.maxSize;
    }

    public static CaffeineKVCacheConfig defaultConfig() {
        CaffeineKVCacheConfig defaultConfig = new CaffeineKVCacheConfig();
        defaultConfig.setLogicTtlSecs(300L);
        defaultConfig.setPhysicalTtlSecs(7 * 24 * 60 * 60L);
        defaultConfig.setTtlRandomPct(0.15);
        defaultConfig.setMaxSize(10000);
        return defaultConfig;
    }
}
