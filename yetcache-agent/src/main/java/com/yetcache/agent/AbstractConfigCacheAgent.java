package com.yetcache.agent;

import com.yetcache.core.cache.flathash.*;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Map;

/**
 * 平台统一结构包装基类，提供增强器链注入与代理委托能力。
 * 用于业务侧构建如：UserConfigFlatHashCacheAgent、StockMetaAgent 等。
 *
 * @author walter
 * @since 2025/6/28
 */
public abstract class AbstractConfigCacheAgent<F, V> {
    protected final MultiTierFlatHashCache<F, V> delegate;

    protected AbstractConfigCacheAgent(MultiTierFlatHashCacheConfig config, MeterRegistry meterRegistry) {
        KeyConverter<Void> keyConverter = KeyConverterFactory.createDefault(config.getSpec().getKeyPrefix(),
                config.getSpec().getTenantMode(), config.getSpec().getUseHashTag());

        MultiTierFlatHashCache<F, V> cache = new BaseMultiTierFlatHashCache<>(config.getSpec().getCacheName(),
                config, keyConverter, getFieldConverter());

        MultiTierFlatHashCacheBehaviorEnhancer<F, V> penetrationProtectEnhancer =
                new FlatHashPenetrationProtectEnhancer<>(config.getSpec().getCacheName(), getFieldConverter());

        FlatHashCacheMetricsEnhancer<F, V> cacheMetricsEnhancer =
                new FlatHashCacheMetricsEnhancer<>(config.getSpec().getCacheName(), getFieldConverter(),
                        new MicrometerCacheMetricsCollector(meterRegistry));

        cache = penetrationProtectEnhancer.enhance(cache, config.getEnhance());
        cache = cacheMetricsEnhancer.enhance(cache, config.getEnhance());

        this.delegate = cache;
    }

    protected abstract String getName();

    protected abstract FieldConverter<F> getFieldConverter();

    public V get(F field) {
        return delegate.get(field);
    }

    public Map<F, V> listAll() {
        return delegate.listAll();
    }
}