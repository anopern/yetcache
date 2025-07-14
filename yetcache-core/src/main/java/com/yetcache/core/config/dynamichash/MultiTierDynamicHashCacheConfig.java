package com.yetcache.core.config.dynamichash;

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
public class MultiTierDynamicHashCacheConfig {
    private MultiTierDynamicHashCacheSpec spec;
    protected CaffeineDynamicHashCacheConfig local;
    protected DynamicHashCacheEnhanceConfig enhance;

    public MultiTierDynamicHashCacheConfig(MultiTierDynamicHashCacheConfig other) {
        if (other == null) return;
        this.spec = other.spec != null ? new MultiTierDynamicHashCacheSpec(other.spec) : null;
        this.local = other.local != null ? new CaffeineDynamicHashCacheConfig(other.local) : null;
        this.enhance = other.enhance != null ? new DynamicHashCacheEnhanceConfig(other.enhance) : null;
    }


    public static MultiTierDynamicHashCacheConfig defaultConfig() {
        return new MultiTierDynamicHashCacheConfig(MultiTierDynamicHashCacheSpec.defaultSpec(),
                CaffeineDynamicHashCacheConfig.defaultConfig(), DynamicHashCacheEnhanceConfig.defaultConfig());
    }
}
