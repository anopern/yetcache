package com.yetcache.core.cache.manager;

import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.cache.kv.MultiTierKVCache;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import com.yetcache.core.config.singlehash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.merger.CacheConfigMerger;
import com.yetcache.core.support.field.CacheFieldConverter;
import com.yetcache.core.support.field.CacheFieldConverterFactory;
import com.yetcache.core.support.key.CacheKeyConverter;
import com.yetcache.core.support.key.CacheKeyConverterFactory;
import com.yetcache.core.support.tenant.TenantProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Slf4j
@Component
public final class FlatHashCacheManager {
    private final YetCacheProperties properties;
    private final FlatHashCacheRegistry registry;
    private final TenantProvider tenantProvider;

    public FlatHashCacheManager(YetCacheProperties properties, FlatHashCacheRegistry registry) {
        this(properties, registry, null);
    }

    public FlatHashCacheManager(YetCacheProperties properties, FlatHashCacheRegistry registry, TenantProvider tenantProvider) {
        this.properties = properties;
        this.registry = registry;
        this.tenantProvider = tenantProvider;
    }

    public <K, F, V> MultiTierFlatHashCache<K, F, V> create(String name,
                                                            RedissonClient rClient,
                                                            FlatHashCacheLoader<K, F, V> cacheLoader) {
        return create(name, rClient, null, cacheLoader);
    }

    @SuppressWarnings("unchecked")
    public <K, F, V> MultiTierFlatHashCache<K, F, V> create(String name,
                                                            RedissonClient rClient,
                                                            TenantProvider tenantProvider,
                                                            FlatHashCacheLoader<K, F, V> cacheLoader) {
        MultiTierFlatHashCache<?, ?, ?> existing = registry.get(name);
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
        CacheKeyConverter<K> cacheKeyConverter = CacheKeyConverterFactory.create(config.getKeyPrefix(),
                config.getTenantMode(), config.getUseHashTag(), providerToUse);
        CacheFieldConverter<F> cacheFieldConverter = CacheFieldConverterFactory.create();
        MultiTierFlatHashCache<K, F, V> newCache = new MultiTierFlatHashCache<>(name, config, rClient, cacheLoader,
                cacheKeyConverter, cacheFieldConverter);
        registry.register(name, newCache);
        log.info("FlatHashCache [{}] created and registered", name);
        return newCache;
    }

}
