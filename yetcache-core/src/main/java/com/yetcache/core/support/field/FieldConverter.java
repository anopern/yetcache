package com.yetcache.core.support.field;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface FieldConverter {
    <T> String convert(T bizField);

    <T> T revert(String field);
}
