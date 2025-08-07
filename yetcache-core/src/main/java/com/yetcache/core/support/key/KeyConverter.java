package com.yetcache.core.support.key;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@FunctionalInterface
public interface KeyConverter {
    String convert(Object bizKey);
}
