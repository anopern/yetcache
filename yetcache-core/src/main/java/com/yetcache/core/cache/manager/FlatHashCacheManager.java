package com.yetcache.core.cache.manager;

import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.singlehash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.merger.CacheConfigMerger;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.field.FieldConverterFactory;
import com.yetcache.core.support.key.BizKeyConverter;
import com.yetcache.core.support.key.DefaultBizKeyConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import com.yetcache.core.support.tenant.TenantProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private YetCacheProperties properties;
    @Autowired
    private FlatHashCacheRegistry registry;
    @Autowired(required = false)
    private TenantProvider tenantProvider;

    public <K, F, V> MultiTierFlatHashCache<K, F, V> create(String name,
                                                            RedissonClient rClient,
                                                            FlatHashCacheLoader<K, F, V> cacheLoader) {
        return create(name, rClient, cacheLoader, null, null);
    }

    public <K, F, V> MultiTierFlatHashCache<K, F, V> create(String name,
                                                            RedissonClient rClient,
                                                            FlatHashCacheLoader<K, F, V> cacheLoader,
                                                            BizKeyConverter<K> bizKeyConverter) {
        return create(name, rClient, cacheLoader, bizKeyConverter, null);
    }

    @SuppressWarnings("unchecked")
    public <K, F, V> MultiTierFlatHashCache<K, F, V> create(String name,
                                                            RedissonClient rClient,
                                                            FlatHashCacheLoader<K, F, V> cacheLoader,
                                                            BizKeyConverter<K> bizKeyConverter,
                                                            FieldConverter<F> fieldConverter) {
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
        KeyConverter<K> keyConverter;
        if (null == bizKeyConverter) {
            keyConverter = KeyConverterFactory.createNoneBizKey(config.getKeyPrefix(), config.getTenantMode(),
                    providerToUse);
        } else {
            keyConverter = KeyConverterFactory.createDefault(config.getKeyPrefix(),
                    config.getTenantMode(), config.getUseHashTag(), providerToUse, new DefaultBizKeyConverter<>());
        }
        if (null == fieldConverter) {
            fieldConverter = FieldConverterFactory.create();
        }
        MultiTierFlatHashCache<K, F, V> newCache = new MultiTierFlatHashCache<>(name, config, rClient, cacheLoader,
                keyConverter, fieldConverter);
        registry.register(name, newCache);
        log.info("FlatHashCache [{}] created and registered", name);
        return newCache;
    }

}
