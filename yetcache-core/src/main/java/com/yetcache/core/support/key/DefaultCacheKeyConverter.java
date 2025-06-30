package com.yetcache.core.support.key;

import cn.hutool.core.util.StrUtil;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class DefaultCacheKeyConverter<K> implements CacheKeyConverter<K> {
    private final String keyPrefix;
    private final TenantMode tenantMode;
    private final boolean useHashTag;
    private final TenantProvider tenantProvider;

    public DefaultCacheKeyConverter(String keyPrefix,
                                    TenantMode tenantMode,
                                    boolean useHashTag,
                                    TenantProvider tenantProvider) {
        this.keyPrefix = keyPrefix;
        this.tenantMode = tenantMode;
        this.useHashTag = useHashTag;
        this.tenantProvider = tenantProvider;

        if ((tenantMode == TenantMode.REQUIRED || tenantMode == TenantMode.OPTIONAL) && tenantProvider == null) {
            throw new IllegalArgumentException("TenantProvider must be provided when tenantMode != NONE");
        }
    }

    @Override
    public String convert(K bizKey) {
        if (bizKey == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }

        StringBuilder sb = new StringBuilder(keyPrefix);

        // 拼接租户信息（如配置要求）
        String tenantCode = resolveTenantCode();
        if (tenantCode != null) {
            sb.append(":").append(tenantCode);
        }

        String bizKeyStr = String.valueOf(bizKey);
        if (useHashTag) {
            sb.append("{").append(bizKeyStr).append("}");
        } else {
            sb.append(bizKeyStr);
        }

        return sb.toString();
    }

    /**
     * 根据租户模式，解析并校验租户编码。
     *
     * @return 租户编码，若无需求则返回 null
     */
    private String resolveTenantCode() {
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
