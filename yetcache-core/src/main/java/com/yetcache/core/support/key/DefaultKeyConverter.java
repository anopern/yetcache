package com.yetcache.core.support.key;

import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class DefaultKeyConverter<K> extends AbstractKeyConverter implements KeyConverter<K> {
    protected final boolean useHashTag;
    protected final BizKeyConverter<K> bizKeyConverter;

    public DefaultKeyConverter(String keyPrefix, TenantMode tenantMode,
                               boolean useHashTag, TenantProvider tenantProvider,
                               BizKeyConverter<K> bizKeyConverter) {
        super(keyPrefix, tenantMode, tenantProvider);
        this.useHashTag = useHashTag;
        this.bizKeyConverter = bizKeyConverter;

        if ((tenantMode == TenantMode.REQUIRED || tenantMode == TenantMode.OPTIONAL) && tenantProvider == null) {
            throw new IllegalArgumentException("TenantProvider must be provided when tenantMode != NONE");
        }
    }

    @Override
    public String convert(K bizKey) {
        if (bizKey == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        StringBuilder sb = new StringBuilder(resolvePrefix());

        String bizKeyPart = bizKeyConverter.convert(bizKey);
        if (useHashTag) {
            sb.append("{").append(bizKeyPart).append("}");
        } else {
            sb.append(bizKeyPart);
        }

        return sb.toString();
    }
}
