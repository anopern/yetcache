package com.yetcache.core.config.kv;

import com.yetcache.core.config.CacheLevel;
import com.yetcache.core.config.TenantMode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/5
 */
@Data
@NoArgsConstructor
public class MultiLevelKVCacheSpec {
    protected String cacheName;
    protected String keyPrefix;
    protected CacheLevel cacheLevel;
    protected TenantMode tenantMode;
    protected Boolean useHashTag;

    public MultiLevelKVCacheSpec(CacheLevel cacheLevel, TenantMode tenantMode, Boolean useHashTag) {
        this.cacheLevel = cacheLevel;
        this.tenantMode = tenantMode;
        this.useHashTag = useHashTag;
    }

    public static MultiLevelKVCacheSpec defaultSpec() {
        return new MultiLevelKVCacheSpec(CacheLevel.BOTH, TenantMode.NONE, true);
    }
}
