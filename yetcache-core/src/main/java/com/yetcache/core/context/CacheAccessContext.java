package com.yetcache.core.context;

import com.yetcache.core.support.trace.flashhash.FlatHashCacheAccessTrace;
import com.yetcache.core.support.trace.kv.KVCacheAccessTrace;
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
        private KVCacheAccessTrace<?> kvTrace;
        private FlatHashCacheAccessTrace<?> flatHashTrace;
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

    public static void setSourceNormal() {
        CONTEXT.get().setSource(CacheAccessSources.NORMAL.name());
    }
    public static void setSourceRefresh() {
        CONTEXT.get().setSource(CacheAccessSources.REFRESH.name());
    }

    public static void setSourcePreload() {
        CONTEXT.get().setSource(CacheAccessSources.PRELOAD.name());
    }

    public static void setFlatHashTrace(FlatHashCacheAccessTrace<?> trace) {
        CONTEXT.get().setFlatHashTrace(trace);
    }

    @SuppressWarnings("unchecked")
    public static <F> FlatHashCacheAccessTrace<F> getFlatHashTrace() {
        return (FlatHashCacheAccessTrace<F>) CONTEXT.get().getFlatHashTrace();
    }
}
