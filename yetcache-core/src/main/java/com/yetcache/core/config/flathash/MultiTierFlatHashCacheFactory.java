package com.yetcache.core.config.flathash;

import com.yetcache.core.cache.flathash.BaseMultiTierFlatHashCache;
import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
/**
 * @author walter.yan
 * @since 2025/7/12
 */
public class MultiTierFlatHashCacheFactory {
    public static <F, V> MultiTierFlatHashCache<F, V> create(String cacheName,
                                                             MultiTierFlatHashCacheConfig config,
                                                             KeyConverter<Void> keyConverter,
                                                             FieldConverter<F> fieldConverter) {
        return new BaseMultiTierFlatHashCache<>(cacheName, config, keyConverter, fieldConverter);
    }
}
