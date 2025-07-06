package com.yetcache.core.context;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public class CacheAccessContext {
    private static final ThreadLocal<Context> CONTEXT = ThreadLocal.withInitial(Context::new);

    @Data
    public static class Context {
        private String traceId;
        private String tenantCode;
        private String source;
        private String operator;
        private boolean forceRefresh;
        private boolean broadcastAfterRefresh;
    }
}
