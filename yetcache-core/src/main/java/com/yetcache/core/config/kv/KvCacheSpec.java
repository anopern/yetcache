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
public class KvCacheSpec {
    protected String cacheName;
    protected String keyPrefix;
    protected CacheLevel cacheLevel;
    protected TenantMode tenantMode;
    protected Boolean useHashTag;
    protected Boolean allowNullValue;

    public KvCacheSpec(CacheLevel cacheLevel, TenantMode tenantMode, Boolean useHashTag, Boolean allowNullValue) {
        this.cacheLevel = cacheLevel;
        this.tenantMode = tenantMode;
        this.useHashTag = useHashTag;
        this.allowNullValue = allowNullValue;
    }

    public static KvCacheSpec defaultSpec() {
        return new KvCacheSpec(CacheLevel.BOTH, TenantMode.NONE, true, false);
    }
}
