package com.yetcache.core.result;

/**
 * 缓存系统统一结果封装组件
 *
 * @author walter.yan
 * @since 2025/8/6
 */
public interface CacheResult {
    int code();

    String message();

    Object value();

    HitLevelInfo hitLevelInfo();

    FreshnessInfo freshnessInfo();

    ErrorInfo errorInfo();

    boolean isSuccess();
}
