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

    public MultiTierFlatHashCacheConfig(MultiTierFlatHashCacheConfig other) {
        if (other == null) return;
        this.spec = other.spec != null ? new MultiTierFlatHashCacheSpec(other.spec) : null;
        this.local = other.local != null ? new CaffeineFlatHashCacheConfig(other.local) : null;
        this.enhance = other.enhance != null ? new FlatHashCacheEnhanceConfig(other.enhance) : null;
    }


    public static MultiTierFlatHashCacheConfig defaultConfig() {
        return new MultiTierFlatHashCacheConfig(MultiTierFlatHashCacheSpec.defaultSpec(),
                CaffeineFlatHashCacheConfig.defaultConfig(), FlatHashCacheEnhanceConfig.defaultConfig());
    }
}
