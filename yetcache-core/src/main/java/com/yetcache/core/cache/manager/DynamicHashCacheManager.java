package com.yetcache.core.cache.manager;

import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.cache.loader.DynamicHashCacheLoader;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.config.MultiTierDynamicHashCacheConfig;
import com.yetcache.core.config.MultiTierFlatHashCacheConfig;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.merger.CacheConfigMerger;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.field.FieldConverterFactory;
import com.yetcache.core.support.key.*;
import com.yetcache.core.support.tenant.TenantProvider;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
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
public final class DynamicHashCacheManager {
    @Autowired
    private YetCacheProperties properties;
    @Autowired
    private DynamicHashCacheRegistry registry;
    @Autowired(required = false)
    private TenantProvider tenantProvider;

    public <K, F, V> MultiTierDynamicHashCache<K, F, V> create(String name,
                                                               RedissonClient rClient,
                                                               DynamicHashCacheLoader<K, F, V> loader) {
        return create(name, rClient, null, null, loader);
    }

    @SuppressWarnings("unchecked")
    public <K, F, V> MultiTierDynamicHashCache<K, F, V> create(String name,
                                                               RedissonClient rClient,
                                                               BizKeyConverter<K> bizKeyConverter,
                                                               FieldConverter<F> fieldConverter,
                                                               DynamicHashCacheLoader<K, F, V> loader) {
        MultiTierDynamicHashCache<?, ?, ?> existing = registry.get(name);
        if (existing != null) {
            throw new IllegalStateException("Cache already exists: " + name);
        }

        MultiTierDynamicHashCacheConfig raw = Optional.ofNullable(properties.getCaches().getDynamicHash())
                .map(m -> m.get(name))
                .orElse(null);

        if (raw == null) {
            log.warn("Cache config not found for [{}], using global defaults", name);
            throw new IllegalStateException("Cache config not found for: " + name);
        }

        MultiTierDynamicHashCacheConfig config = CacheConfigMerger.merge(properties.getGlobal(), raw);
        // 在 merge 之后，填充 default 的 ttlRandomPercent（只做一次性补全）
        if (config.getLocal() != null && config.getLocal().getTtlRandomPercent() == null) {
            config.getLocal().setTtlRandomPercent(config.getTtlRandomPercent());
        }
        if (config.getRemote() != null && config.getRemote().getTtlRandomPercent() == null) {
            config.getRemote().setTtlRandomPercent(config.getTtlRandomPercent());
        }
        TenantProvider providerToUse = config.getTenantMode() == TenantMode.NONE ? null : this.tenantProvider;
        KeyConverter<K> keyConverter = KeyConverterFactory.createDefault(config.getKeyPrefix(),
                config.getTenantMode(), config.getUseHashTag(), providerToUse,
                Objects.requireNonNullElseGet(bizKeyConverter, DefaultBizKeyConverter::new));

        if (null == fieldConverter) {
            fieldConverter = FieldConverterFactory.create();
        }
        MultiTierDynamicHashCache<K, F, V> newCache = new MultiTierDynamicHashCache<>(name, config, rClient,
                keyConverter, fieldConverter, loader);
        registry.register(name, newCache);
        log.info("FlatHashCache [{}] created and registered", name);
        return newCache;
    }

}
