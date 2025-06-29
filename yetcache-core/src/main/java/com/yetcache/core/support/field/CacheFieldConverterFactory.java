package com.yetcache.core.support.field;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class CacheFieldConverterFactory {
    public static <K> CacheFieldConverter<K> create() {
        return new AbstractCacheFieldConverter<>();
    }
}
