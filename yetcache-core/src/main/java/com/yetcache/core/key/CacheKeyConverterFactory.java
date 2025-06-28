package com.yetcache.core.key;

import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class CacheKeyConverterFactory {
    public static <K> CacheKeyConverter<K> create(String keyPrefix, boolean useTenant, boolean useHashTag,
                                                  Supplier<String> tenantCodeSupplier) {
        return new DefaultCacheKeyConverter<>(keyPrefix, useTenant, useHashTag, tenantCodeSupplier);
    }
}
