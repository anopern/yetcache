package com.yetcache.example.tenant;

import com.yetcache.core.context.CacheAccessContext;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
@Component
public class TenantContextHolder {
    private static final ThreadLocal<CacheAccessContext.Context> CONTEXT = ThreadLocal.withInitial(CacheAccessContext.Context::new);


    @Data
    static final class Context {
        private String tenantCode;
    }

    public static void setTenantCode(String tenantCode) {
        CONTEXT.get().setTenantCode(tenantCode);
    }

    public static String getTenantCode() {
        return CONTEXT.get().getTenantCode();
    }
}
