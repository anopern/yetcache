package com.yetcache.core.support.key;

import cn.hutool.core.util.StrUtil;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class DefaultKeyConverter<K> implements KeyConverter<K> {
    protected final String keyPrefix;
    protected final TenantMode tenantMode;
    protected final boolean useHashTag;
    protected final TenantProvider tenantProvider;
    protected final BizKeyPartConverter<K> bizKeyPartConverter;

    public DefaultKeyConverter(String keyPrefix, TenantMode tenantMode,
                               boolean useHashTag, TenantProvider tenantProvider,
                               BizKeyPartConverter<K> bizKeyPartConverter) {
        this.keyPrefix = keyPrefix;
        this.tenantMode = tenantMode;
        this.useHashTag = useHashTag;
        this.tenantProvider = tenantProvider;
        this.bizKeyPartConverter = bizKeyPartConverter;

        if ((tenantMode == TenantMode.REQUIRED || tenantMode == TenantMode.OPTIONAL) && tenantProvider == null) {
            throw new IllegalArgumentException("TenantProvider must be provided when tenantMode != NONE");
        }
    }

    @Override
    public String convert(K bizKey) {
        if (bizKey == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        StringBuilder sb = new StringBuilder(getKeyPrefixWithTenant());

        String bizKeyPart = bizKeyPartConverter.convert(bizKey);
        if (useHashTag) {
            sb.append("{").append(bizKeyPart).append("}");
        } else {
            sb.append(bizKeyPart);
        }

        return sb.toString();
    }

    protected String getKeyPrefixWithTenant() {
        StringBuilder sb = new StringBuilder(keyPrefix);
        String tenantCode = resolveTenantCode();
        if (tenantCode != null) {
            sb.append(":").append(tenantCode);
        }
        return sb.toString();
    }

    /**
     * 根据租户模式，解析并校验租户编码。
     *
     * @return 租户编码，若无需求则返回 null
     */
    protected String resolveTenantCode() {
        switch (tenantMode) {
            case REQUIRED:
                String requiredCode = tenantProvider.getCurrentTenantCode();
                if (StrUtil.isBlank(requiredCode)) {
                    throw new IllegalStateException("Tenant code is required but not provided");
                }
                return requiredCode;

            case OPTIONAL:
                String optionalCode = tenantProvider.getDefaultTenantCode();
                if (StrUtil.isBlank(optionalCode)) {
                    throw new IllegalStateException("Tenant code is optional but no default code provided");
                }
                return optionalCode;

            case NONE:
            default:
                return null;
        }
    }
}
