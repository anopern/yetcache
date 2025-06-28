package com.yetcache.core.key;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@FunctionalInterface
public interface CacheKeyConverter<K> {
    String convert(K key);
}
