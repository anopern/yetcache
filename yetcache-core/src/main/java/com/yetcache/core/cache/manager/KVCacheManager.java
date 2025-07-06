package com.yetcache.core.cache.manager;

import com.yetcache.core.config.TenantMode;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.kv.MultiTierKVCacheSpec;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.cache.kv.MultiTierKVCache;
import com.yetcache.core.support.tenant.TenantProvider;
import com.yetcache.core.merger.CacheConfigMerger;
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
public final class KVCacheManager {
    @Autowired
    private YetCacheProperties properties;
    @Autowired
    private KVCacheRegistry registry;
    @Autowired(required = false)
    private TenantProvider tenantProvider;

    @SuppressWarnings("unchecked")
    public <K, V> MultiTierKVCache<K, V> create(String name,
                                                RedissonClient rClient,
                                                KVCacheLoader<K, V> cacheLoader) {
        return create(name, rClient, null, cacheLoader);
    }

    @SuppressWarnings("unchecked")
    public <K, V> MultiTierKVCache<K, V> create(String name,
                                                RedissonClient rClient,
                                                KeyConverter<K> keyConverter,
                                                KVCacheLoader<K, V> cacheLoader) {
        MultiTierKVCache<?, ?> existing = registry.get(name);
        if (existing != null) {
            throw new IllegalStateException("Cache already exists: " + name);
        }

        MultiTierKVCacheConfig raw = Optional.ofNullable(properties.getCaches().getKv())
                .map(m -> m.get(name))
                .orElse(new MultiTierKVCacheConfig());
        MultiTierKVCacheConfig config = CacheConfigMerger.merge(properties.getGlobal(), raw);

        MultiTierKVCacheSpec spec = config.getSpec();
        TenantProvider providerToUse = spec.getTenantMode() == TenantMode.NONE ? null : this.tenantProvider;

        if (null == keyConverter) {
            keyConverter = KeyConverterFactory.createDefault(spec.getKeyPrefix(), spec.getTenantMode(),
                    spec.getUseHashTag(), providerToUse);
        }
        MultiTierKVCache<K, V> newCache = new MultiTierKVCache<>(name, config, rClient, keyConverter, cacheLoader);
        registry.register(name, newCache);
        log.info("KVCache [{}] created and registered", name);
        return newCache;
    }

}
