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
public class FlatHashCacheConfig {
    private FlatHashCacheSpec spec;
    protected CaffeineFlatHashCacheConfig local;
    protected FlatHashCacheEnhanceConfig enhance;

    public FlatHashCacheConfig(FlatHashCacheConfig other) {
        if (other == null) return;
        this.spec = other.spec != null ? new FlatHashCacheSpec(other.spec) : null;
        this.local = other.local != null ? new CaffeineFlatHashCacheConfig(other.local) : null;
        this.enhance = other.enhance != null ? new FlatHashCacheEnhanceConfig(other.enhance) : null;
    }


    public static FlatHashCacheConfig defaultConfig() {
        return new FlatHashCacheConfig(FlatHashCacheSpec.defaultSpec(),
                CaffeineFlatHashCacheConfig.defaultConfig(), FlatHashCacheEnhanceConfig.defaultConfig());
    }
}
