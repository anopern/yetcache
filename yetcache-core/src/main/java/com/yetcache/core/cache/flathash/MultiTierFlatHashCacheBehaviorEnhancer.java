package com.yetcache.core.cache.flathash;

import com.yetcache.core.config.flathash.FlatHashCacheEnhanceConfig;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public interface MultiTierFlatHashCacheBehaviorEnhancer<F, V> {
    MultiTierFlatHashCache<F, V> enhance(MultiTierFlatHashCache<F, V> origin, FlatHashCacheEnhanceConfig config);
}