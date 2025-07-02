package com.yetcache.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiTierDynamicHashCacheConfig extends BaseMultiTierCacheConfig {
    protected String keyPrefix;

    protected Boolean enableLoadFallbackOnMiss = false;
}
