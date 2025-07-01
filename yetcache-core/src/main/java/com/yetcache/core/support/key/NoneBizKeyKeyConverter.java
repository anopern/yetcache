package com.yetcache.core.support.key;

import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
public class NoneBizKeyKeyConverter<K> extends AbstractKeyConverter<K> {

    public NoneBizKeyKeyConverter(String keyPrefix, TenantMode tenantMode, TenantProvider tenantProvider) {
        super(keyPrefix, tenantMode, tenantProvider);
    }

    @Override
    public String convert(K bizKey) {
        if (null != bizKey) {
            throw new IllegalArgumentException("Cache key must be null");
        }
        return getKeyPrefixWithTenant();
    }
}
