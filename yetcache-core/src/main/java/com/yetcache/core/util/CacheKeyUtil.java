package com.yetcache.core.util;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
public class CacheKeyUtil {
    public static String joinLogicalKey(String key, String field) {
        return key + "::" + field;
    }
}
