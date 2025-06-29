package com.yetcache.core.cache.manager;

import com.yetcache.core.config.MultiTierCacheConfig;
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
import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Slf4j
@Component
public class CacheManager {
    protected final YetCacheProperties properties;
    protected final CacheRegistry registry;

    public CacheManager(YetCacheProperties properties, CacheRegistry registry) {
        this.properties = properties;
        this.registry = registry;
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

        MultiTierCacheConfig raw = Optional.ofNullable(properties.getCaches().getKv())
                .map(m -> m.get(name))
                .orElse(null);

        if (raw == null) {
            log.warn("Cache config not found for [{}], using global defaults", name);
            throw new IllegalStateException("Cache config not found for: " + name);
        }

        MultiTierCacheConfig config = CacheConfigMerger.merge(properties.getGlobal(), raw);

        Supplier<String> tenantCodeSupplier = () -> Optional.ofNullable(tenantProvider)
                .map(TenantProvider::getTenantCode)
                .orElse(null);
        CacheKeyConverter<K> cacheKeyConverter = CacheKeyConverterFactory.create(config.getKeyPrefix(),
                config.getTenantMode().useTenant(), config.getUseHashTag(), tenantCodeSupplier);
        MultiTierKVCache<K, V> newCache = new MultiTierKVCache<>(name, config, rClient, cacheLoader, cacheKeyConverter);
        registry.register(name, newCache);
        log.info("KVCache [{}] created and registered", name);
        return newCache;
    }

}
