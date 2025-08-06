package com.yetcache.core.result;

import java.util.Map;

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

    HitTierInfo hitTierInfo();

    Throwable error();

    Map<String, Object> metadata();

    boolean isSuccess();
}
