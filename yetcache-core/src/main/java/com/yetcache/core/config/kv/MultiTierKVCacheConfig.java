package com.yetcache.core.config.kv;

import com.yetcache.core.config.BaseMultiTierCacheConfig;
import com.yetcache.core.config.CacheTier;
import com.yetcache.core.config.TenantMode;
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
public class MultiTierKVCacheConfig extends BaseMultiTierCacheConfig {

    protected String keyPrefix;

    protected CaffeineKVCacheConfig local;
    protected RedisKVCacheConfig remote;
}
