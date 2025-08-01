package com.yetcache.core.support.field;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface CacheFieldConverter<K> {
    String convert(K bizField);
    K reverse(String field);
}
