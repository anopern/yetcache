package com.yetcache.core.cache.manager;

import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.singlehash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.merger.CacheConfigMerger;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.field.FieldConverterFactory;
import com.yetcache.core.support.key.BizKeyPartConverter;
import com.yetcache.core.support.key.DefaultBizKeyPartConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import com.yetcache.core.support.tenant.TenantProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Objects;
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
        return create(name, rClient, cacheLoader, null);
    }

    @SuppressWarnings("unchecked")
    public <K, F, V> MultiTierFlatHashCache<K, F, V> create(String name,
                                                            RedissonClient rClient,
                                                            FlatHashCacheLoader<K, F, V> cacheLoader,
                                                            BizKeyPartConverter<K> bizKeyPartConverter) {
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
        KeyConverter<K> keyConverter = KeyConverterFactory.createDefault(config.getKeyPrefix(),
                config.getTenantMode(), config.getUseHashTag(), providerToUse,
                Objects.requireNonNullElseGet(bizKeyPartConverter, DefaultBizKeyPartConverter::new));
        FieldConverter<F> fieldConverter = FieldConverterFactory.create();
        MultiTierFlatHashCache<K, F, V> newCache = new MultiTierFlatHashCache<>(name, config, rClient, cacheLoader,
                keyConverter, fieldConverter);
        registry.register(name, newCache);
        log.info("FlatHashCache [{}] created and registered", name);
        return newCache;
    }

}
