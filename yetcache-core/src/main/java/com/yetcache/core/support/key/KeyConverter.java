package com.yetcache.core.support.key;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public interface KeyConverter {
    <T> String convert(T bizKey);

    <T> T revert(String key);
}
