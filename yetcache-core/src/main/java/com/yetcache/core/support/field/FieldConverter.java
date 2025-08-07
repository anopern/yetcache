package com.yetcache.core.support.field;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface FieldConverter {
    String convert(Object bizField);

    Object revert(String field);
}
