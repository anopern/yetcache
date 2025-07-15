package com.yetcache.core.support.key;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class KeyConverterFactory {
    public static <K> KeyConverter<K> createDefault(String keyPrefix, boolean useHashTag) {
        return new DefaultKeyConverter<>(keyPrefix, useHashTag);
    }
}
