package com.yetcache.core.support.key;

import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;

import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class CacheKeyConverterFactory {
    public static <K> CacheKeyConverter<K> create(String keyPrefix, TenantMode tenantMode, boolean useHashTag,
                                                  TenantProvider tenantProvider) {
        return new DefaultCacheKeyConverter<>(keyPrefix, tenantMode, useHashTag, tenantProvider);
    }
}
