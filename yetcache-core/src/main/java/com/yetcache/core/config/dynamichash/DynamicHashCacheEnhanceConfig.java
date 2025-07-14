package com.yetcache.core.config.dynamichash;

import com.yetcache.core.config.HitCountMetricsConfig;
import com.yetcache.core.config.PenetrationProtectConfig;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Data
public class DynamicHashCacheEnhanceConfig {
    protected PenetrationProtectConfig penetrationProtect;
    private HitCountMetricsConfig hitMetrics;

    public DynamicHashCacheEnhanceConfig() {
    }

    public DynamicHashCacheEnhanceConfig(DynamicHashCacheEnhanceConfig other) {
        if (other == null) return;
        this.penetrationProtect = other.penetrationProtect != null
                ? new PenetrationProtectConfig(other.penetrationProtect)
                : null;
        this.hitMetrics = other.hitMetrics != null
                ? new HitCountMetricsConfig(other.hitMetrics)
                : null;
    }

    public static DynamicHashCacheEnhanceConfig defaultConfig() {
        DynamicHashCacheEnhanceConfig config = new DynamicHashCacheEnhanceConfig();
        config.setPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        config.setHitMetrics(HitCountMetricsConfig.defaultConfig());
        return config;
    }
}
