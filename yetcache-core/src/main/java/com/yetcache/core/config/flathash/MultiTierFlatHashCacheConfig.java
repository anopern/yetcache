package com.yetcache.core.config.flathash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiTierFlatHashCacheConfig {
    private MultiTierFlatHashCacheSpec spec;
    protected CaffeineFlatHashCacheConfig local;
    protected FlatHashCacheEnhanceConfig enhance;

    public static MultiTierFlatHashCacheConfig defaultConfig() {
        return new MultiTierFlatHashCacheConfig(MultiTierFlatHashCacheSpec.defaultSpec(),
                CaffeineFlatHashCacheConfig.defaultConfig(), FlatHashCacheEnhanceConfig.defaultConfig());
    }
}
