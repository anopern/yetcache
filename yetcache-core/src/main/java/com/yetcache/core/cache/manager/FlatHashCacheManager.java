package com.yetcache.core.cache.manager;

import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.MultiTierFlatHashCacheConfig;
import com.yetcache.core.merger.CacheConfigMerger;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.field.FieldConverterFactory;
import com.yetcache.core.support.key.*;
import com.yetcache.core.support.tenant.TenantProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Slf4j
@Component
public final class FlatHashCacheManager {
    @Autowired
    private YetCacheProperties properties;
    @Autowired
    private FlatHashCacheRegistry registry;
    @Autowired(required = false)
    private TenantProvider tenantProvider;

    public <F, V> MultiTierFlatHashCache<F, V> create(String name,
                                                      RedissonClient rClient,
                                                      FlatHashCacheLoader<F, V> cacheLoader) {
        return create(name, rClient, cacheLoader, null);
    }

    @SuppressWarnings("unchecked")
    public <F, V> MultiTierFlatHashCache<F, V> create(String name,
                                                      RedissonClient rClient,
                                                      FlatHashCacheLoader<F, V> cacheLoader,
                                                      FieldConverter<F> fieldConverter) {
        MultiTierFlatHashCache<?, ?> existing = registry.get(name);
        if (existing != null) {
            throw new IllegalStateException("Cache already exists: " + name);
        }

        MultiTierFlatHashCacheConfig raw = Optional.ofNullable(properties.getCaches().getFlatHash())
                .map(m -> m.get(name))
                .orElse(null);

        if (raw == null) {
            log.warn("Cache config not found for [{}], using global defaults", name);
            throw new IllegalStateException("Cache config not found for: " + name);
        }

        MultiTierFlatHashCacheConfig config = CacheConfigMerger.merge(properties.getGlobal(), raw);
        TenantProvider providerToUse = config.getTenantMode() == TenantMode.NONE ? null : this.tenantProvider;
        FlatHashKeyConverter keyConverter = KeyConverterFactory.createNoneBizKey(config.getKey(),
                config.getTenantMode(), providerToUse);

        if (null == fieldConverter) {
            fieldConverter = FieldConverterFactory.create();
        }
        MultiTierFlatHashCache<F, V> newCache = new MultiTierFlatHashCache<>(name, config, rClient, cacheLoader,
                keyConverter, fieldConverter);
        registry.register(name, newCache);
        log.info("FlatHashCache [{}] created and registered", name);
        return newCache;
    }

}
