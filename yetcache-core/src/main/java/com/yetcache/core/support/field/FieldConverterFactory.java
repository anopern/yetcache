package com.yetcache.core.support.field;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class FieldConverterFactory {
    public static <K> FieldConverter<K> create() {
        return new AbstractFieldConverter<>();
    }
}
