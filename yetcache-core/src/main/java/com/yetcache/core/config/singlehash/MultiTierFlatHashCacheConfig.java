package com.yetcache.core.config.singlehash;

import com.yetcache.core.config.BaseMultiTierCacheConfig;
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
public class MultiTierFlatHashCacheConfig extends BaseMultiTierCacheConfig {
    protected String keyPrefix;

    protected CaffeineFlatHashCacheConfig local;
    protected RedisFlatHashCacheConfig remote;

    protected Boolean enableLoadFallbackOnMiss = false;
}
