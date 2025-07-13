package com.yetcache.core.cache.flathash;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public interface CacheAccessMetricsCollector {
    /**
     * 记录一次访问行为。
     *
     * @param cacheName 缓存名称（如 user-id-key-cache）
     * @param method    操作方法名（如 get / refreshAll / listAll）
     * @param result    操作结果（如 hit / miss / block / success / fail）
     */
    void recordAccess(String cacheName, String method, String result);

    /**
     * 记录一次耗时（可选）
     */
    default void recordLatency(String cacheName, String method, long nanos) {
        // 默认不实现
    }
}
