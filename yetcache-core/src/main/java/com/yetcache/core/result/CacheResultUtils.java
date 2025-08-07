package com.yetcache.core.result;

import com.yetcache.core.cache.support.CacheValueHolder;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
public class CacheResultUtils {
    @SuppressWarnings("unchecked")
    public static <T> T getTypedResult(CacheResult result) {
        if (!(result instanceof SingleCacheResult)) {
            throw new IllegalStateException("Not a SingleCacheResult: " + result);
        }
        return (T) result;
    }

    public static <T> Optional<T> optional(CacheResult result, Class<T> clazz) {
        try {
            T value = getTypedResult(result);
            return clazz.isInstance(value) ? Optional.of(clazz.cast(value)) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
