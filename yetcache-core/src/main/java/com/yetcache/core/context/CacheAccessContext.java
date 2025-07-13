package com.yetcache.core.context;

import lombok.Data;

import java.util.UUID;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public class CacheAccessContext {
    private static final ThreadLocal<Context> TL = ThreadLocal.withInitial(Context::new);

    @Data
    public static class Context {
        private String traceId;
        private String tenantCode;
        private String source;
        private String operator;
        private boolean forceRefresh;
        private boolean broadcastAfterRefresh;

        void init(String source) {
            if (traceId == null) {                 // 幂等
                this.traceId = UUID.randomUUID().toString();
                this.source = source;
            }
        }
    }

    public static Context begin(String source) {
        Context ctx = TL.get();
        ctx.init(source);
        return ctx;
    }

    public static String getTenantId() {
        return TL.get().getTenantCode();
    }

    public static void setTenantId(String tenantId) {
        TL.get().setTenantCode(tenantId);
    }

    public static void clear() {
        TL.remove();
    }

}
