package com.yetcache.core.support.tenant;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
public interface TenantProvider {
    String getCurrentTenantCode();
    String getDefaultTenantCode();

}
