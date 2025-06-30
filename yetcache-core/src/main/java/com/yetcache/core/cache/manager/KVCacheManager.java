package com.yetcache.core.cache.manager;

import com.yetcache.core.config.TenantMode;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.support.key.CacheKeyConverter;
import com.yetcache.core.support.key.CacheKeyConverterFactory;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.cache.kv.MultiTierKVCache;
import com.yetcache.core.support.tenant.TenantProvider;
import com.yetcache.core.merger.CacheConfigMerger;
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
public final class KVCacheManager {
    private final YetCacheProperties properties;
    private final KVCacheRegistry registry;
    private final TenantProvider tenantProvider;

    public KVCacheManager(YetCacheProperties properties, KVCacheRegistry registry) {
        this(properties, registry, null);
    }

    public KVCacheManager(YetCacheProperties properties, KVCacheRegistry registry, TenantProvider tenantProvider) {
        this.properties = properties;
        this.registry = registry;
        this.tenantProvider = tenantProvider;
    }

    public <K, V> MultiTierKVCache<K, V> create(String name,
                                                RedissonClient rClient,
                                                KVCacheLoader<K, V> cacheLoader) {
        return create(name, rClient, null, cacheLoader);
    }

    @SuppressWarnings("unchecked")
    public <K, V> MultiTierKVCache<K, V> create(String name,
                                                RedissonClient rClient,
                                                TenantProvider tenantProvider,
                                                KVCacheLoader<K, V> cacheLoader) {
        MultiTierKVCache<?, ?> existing = registry.get(name);
        if (existing != null) {
            throw new IllegalStateException("Cache already exists: " + name);
        }

        MultiTierKVCacheConfig raw = Optional.ofNullable(properties.getCaches().getKv())
                .map(m -> m.get(name))
                .orElse(null);

        if (raw == null) {
            log.warn("Cache config not found for [{}], using global defaults", name);
            throw new IllegalStateException("Cache config not found for: " + name);
        }

        MultiTierKVCacheConfig config = CacheConfigMerger.merge(properties.getGlobal(), raw);

        TenantProvider providerToUse = config.getTenantMode() == TenantMode.NONE ? null : this.tenantProvider;
        CacheKeyConverter<K> cacheKeyConverter = CacheKeyConverterFactory.create(config.getKeyPrefix(),
                config.getTenantMode(), config.getUseHashTag(), providerToUse);
        MultiTierKVCache<K, V> newCache = new MultiTierKVCache<>(name, config, rClient, cacheLoader, cacheKeyConverter);
        registry.register(name, newCache);
        log.info("KVCache [{}] created and registered", name);
        return newCache;
    }

}
