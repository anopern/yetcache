package com.yetcache.core.support.key;

import cn.hutool.core.util.StrUtil;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.context.CacheAccessContext;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
@Data
public abstract class AbstractKeyConverter {
    protected final String keyPrefix;
    protected final TenantMode tenantMode;

    protected String resolvePrefix() {
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
                String requiredId = CacheAccessContext.getTenantId();
                if (StrUtil.isBlank(requiredId)) {
                    throw new IllegalStateException("Tenant id is required but not provided");
                }
                return requiredId;
            case NONE:
            default:
                return null;
        }
    }
}
