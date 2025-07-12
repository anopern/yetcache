package com.yetcache.core.config.flathash;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Data
public class HitCountMetricsConfig {
    private Boolean enabled;

    public static HitCountMetricsConfig defaultConfig() {
        HitCountMetricsConfig config = new HitCountMetricsConfig();
        config.setEnabled(true);
        return config;
    }
}
