package com.yetcache.core.config.flathash;

import com.yetcache.core.config.PenetrationProtectConfig;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Data
public class FlatHashCacheEnhanceConfig {
    protected PenetrationProtectConfig penetrationProtect;
    private HitCountMetricsConfig hitMetrics;

    public FlatHashCacheEnhanceConfig() {
    }

    public FlatHashCacheEnhanceConfig(FlatHashCacheEnhanceConfig other) {
        if (other == null) return;
        this.penetrationProtect = other.penetrationProtect != null
                ? new PenetrationProtectConfig(other.penetrationProtect)
                : null;
        this.hitMetrics = other.hitMetrics != null
                ? new HitCountMetricsConfig(other.hitMetrics)
                : null;
    }

    public static FlatHashCacheEnhanceConfig defaultConfig() {
        FlatHashCacheEnhanceConfig config = new FlatHashCacheEnhanceConfig();
        config.setPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        config.setHitMetrics(HitCountMetricsConfig.defaultConfig());
        return config;
    }
}
