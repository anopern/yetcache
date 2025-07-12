package com.yetcache.agent;

import com.yetcache.core.cache.flathash.*;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 平台统一结构包装基类，提供增强器链注入与代理委托能力。
 * 用于业务侧构建如：UserConfigFlatHashCacheAgent、StockMetaAgent 等。
 *
 * @author walter
 * @since 2025/6/28
 */
@Slf4j
public abstract class AbstractConfigCacheAgent<F, V> implements MultiTierFlatHashRefreshableCacheAgent {
    protected final MultiTierFlatHashCache<F, V> delegate;
    protected final MultiTierFlatHashCacheConfig config;
    protected final FlatHashCacheLoader<F, V> cacheLoader;
    protected final MeterRegistry meterRegistry;

    protected AbstractConfigCacheAgent(MultiTierFlatHashCacheConfig config, FlatHashCacheLoader<F, V> cacheLoader,
                                       MeterRegistry meterRegistry) {
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.meterRegistry = meterRegistry;

        this.delegate = createDelegateCache();
    }

    protected MultiTierFlatHashCache<F, V> createDelegateCache() {
        KeyConverter<Void> keyConverter = KeyConverterFactory.createDefault(config.getSpec().getKeyPrefix(),
                config.getSpec().getUseHashTag());

        MultiTierFlatHashCache<F, V> cache = new BaseMultiTierFlatHashCache<>(config.getSpec().getCacheName(),
                config, keyConverter, getFieldConverter());

        MultiTierFlatHashCacheBehaviorEnhancer<F, V> penetrationProtectEnhancer =
                new FlatHashPenetrationProtectEnhancer<>(config.getSpec().getCacheName(), getFieldConverter());

        FlatHashCacheMetricsEnhancer<F, V> cacheMetricsEnhancer =
                new FlatHashCacheMetricsEnhancer<>(config.getSpec().getCacheName(), getFieldConverter(),
                        new MicrometerCacheAccessMetricsCollector(meterRegistry));

        cache = penetrationProtectEnhancer.enhance(cache, config.getEnhance());
        cache = cacheMetricsEnhancer.enhance(cache, config.getEnhance());
        return cache;
    }

    public abstract String getName();

    protected abstract FieldConverter<F> getFieldConverter();

    public Long getRefreshIntervalSecs() {
        return config.getSpec().getRefreshIntervalSecs();
    }

    protected final FlatHashAccessResult<Map<F, V>> refreshAllWithResult() {
        long start = System.currentTimeMillis();
        try {
            Map<F, V> map = cacheLoader.loadAll();
            if (map == null || map.isEmpty()) {
                return FlatHashAccessResult.fail(new IllegalStateException("Loaded config map is empty"));
            }
            delegate.putAll(map);
            return FlatHashAccessResult.success(map);
        } catch (Exception e) {
            log.warn("[{}] refresh failed: {}", getName(), e.getMessage(), e);
            return FlatHashAccessResult.fail(e);
        } finally {
            long cost = System.currentTimeMillis() - start;
            log.debug("[{}] refresh cost={}ms", getName(), cost);
        }
    }

    public V get(F field) {
        return delegate.get(field);
    }

    public Map<F, V> listAll() {
        return delegate.listAll();
    }

    @Override
    public boolean refreshAll() {
        FlatHashAccessResult<Map<F, V>> result = refreshAllWithResult();
        if (null != result) {
            return result.isSuccess();
        }
        return false;
    }
}