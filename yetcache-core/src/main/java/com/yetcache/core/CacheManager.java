package com.yetcache.core;

import com.yetcache.core.config.MultiTierCacheConfig;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.kv.MultiTierKVCache;
import com.yetcache.core.util.CacheConfigMerger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Slf4j
@Component
public class CacheManager {
    protected final YetCacheProperties properties;
    protected final CacheRegistry cacheRegistry;

    public CacheManager(YetCacheProperties properties, CacheRegistry cacheRegistry) {
        this.properties = properties;
        this.cacheRegistry = cacheRegistry;
    }

    @SuppressWarnings("unchecked")
    public <K, V> MultiTierKVCache<K, V> create(String name) {
        MultiTierKVCache<?, ?> existing = cacheRegistry.get(name);
        if (existing != null) {
            return (MultiTierKVCache<K, V>) existing;
        }

        MultiTierCacheConfig raw = Optional.ofNullable(properties.getCaches().getKv())
                .map(m -> m.get(name))
                .orElse(null);

        if (raw == null) {
            log.warn("Cache config not found for [{}], using global defaults", name);
            raw = new MultiTierCacheConfig(); // 或 fail fast
        }

        MultiTierCacheConfig config = CacheConfigMerger.merge(properties.getGlobal(), raw);

        MultiTierKVCache<K, V> newCache = new MultiTierKVCache<>(config);

        cacheRegistry.register(name, newCache);
        log.info("KVCache [{}] created and registered", name);

        return newCache;
    }

}
