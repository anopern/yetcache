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
    }

    // 设置方法
    public static void setTenantCode(String tenantCode) {
        CONTEXT.get().setTenantCode(tenantCode);
    }

    public static void setSource(String source) {
        CONTEXT.get().setSource(source);
    }

    public static void setTraceId(String traceId) {
        CONTEXT.get().setTraceId(traceId);
    }

    public static void setOperator(String operator) {
        CONTEXT.get().setOperator(operator);
    }

    public static void setForceRefresh(boolean forceRefresh) {
        CONTEXT.get().setForceRefresh(forceRefresh);
    }

    // 获取方法
    public static String getTenantId() {
        return CONTEXT.get().getTenantCode();
    }

    public static String getSource() {
        return CONTEXT.get().getSource();
    }

    public static String getTraceId() {
        return CONTEXT.get().getTraceId();
    }

    public static String getOperator() {
        return CONTEXT.get().getOperator();
    }

    public static boolean isForceRefresh() {
        return CONTEXT.get().isForceRefresh();
    }

    // 清理方法（务必调用）
    public static void clear() {
        CONTEXT.remove();
    }

    // 用于封装式调用（可选）
    public static Context getContext() {
        return CONTEXT.get();
    }

    public static void setContext(Context context) {
        CONTEXT.set(context);
    }
}
