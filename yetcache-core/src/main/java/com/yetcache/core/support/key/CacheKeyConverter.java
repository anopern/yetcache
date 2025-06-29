package com.yetcache.core.support.key;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@FunctionalInterface
public interface CacheKeyConverter<K> {
    String convert(K bizKey);
}
