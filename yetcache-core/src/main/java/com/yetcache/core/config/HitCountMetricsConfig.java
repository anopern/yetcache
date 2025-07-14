package com.yetcache.core.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Data
@NoArgsConstructor
public class HitCountMetricsConfig {
    private Boolean enabled;

    public HitCountMetricsConfig(HitCountMetricsConfig other) {
        if (other == null) return;
        this.enabled = other.enabled;
    }

    public static HitCountMetricsConfig defaultConfig() {
        HitCountMetricsConfig config = new HitCountMetricsConfig();
        config.setEnabled(true);
        return config;
    }
}
