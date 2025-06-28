package com.yetcache.core;

import cn.hutool.core.util.StrUtil;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class CacheKeyUtils {
    public static String buildCacheKey(String keyPrefix, String bizKey, String tenantCode) {
        if (StrUtil.isNotBlank(tenantCode)) {
            return String.format("%s:%s:{%s}", keyPrefix, tenantCode, bizKey);
        } else {
            return String.format("%s:{%s}", keyPrefix, bizKey);
        }
    }
}
