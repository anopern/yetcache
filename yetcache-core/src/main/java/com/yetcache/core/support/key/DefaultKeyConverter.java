package com.yetcache.core.support.key;

import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class DefaultKeyConverter<K> extends AbstractKeyConverter implements KeyConverter<K> {
    protected final boolean useHashTag;

    public DefaultKeyConverter(String keyPrefix, TenantMode tenantMode, boolean useHashTag) {
        super(keyPrefix, tenantMode);
        this.useHashTag = useHashTag;
    }

    @Override
    public String convert(K bizKey) {
        if (bizKey == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        StringBuilder sb = new StringBuilder(resolvePrefix());

        String bizKeyStr = String.valueOf(bizKey);
        if (useHashTag) {
            sb.append("{").append(bizKeyStr).append("}");
        } else {
            sb.append(bizKeyStr);
        }

        return sb.toString();
    }
}
