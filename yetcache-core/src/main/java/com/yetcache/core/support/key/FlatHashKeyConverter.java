package com.yetcache.core.support.key;

import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
public class FlatHashKeyConverter extends AbstractKeyConverter {

    public FlatHashKeyConverter(String keyPrefix, TenantMode tenantMode, TenantProvider tenantProvider) {
        super(keyPrefix, tenantMode, tenantProvider);
    }

    public String convert() {
        return resolvePrefix();
    }
}
