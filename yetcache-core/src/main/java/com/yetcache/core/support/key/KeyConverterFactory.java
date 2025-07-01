package com.yetcache.core.support.key;

import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class KeyConverterFactory {
    public static <K> KeyConverter<K> createDefault(String keyPrefix,
                                                    TenantMode tenantMode,
                                                    boolean useHashTag,
                                                    TenantProvider tenantProvider,
                                                    BizKeyPartConverter<K> bizKeyPartConverter) {
        return new DefaultKeyConverter<>(keyPrefix, tenantMode, useHashTag, tenantProvider, bizKeyPartConverter);
    }
}
