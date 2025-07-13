package com.yetcache.agent.flathash;

import com.yetcache.core.cache.flathash.*;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
@Slf4j
public abstract class AbstractFlatHashCacheAgent<F, V> implements FlatHashCacheAgent<F, V> {
    protected final MultiTierFlatHashCache<F, V> cache;
    protected final MultiTierFlatHashCacheConfig config;
    protected final FlatHashCacheLoader<F, V> cacheLoader;
    protected final CacheAccessMetricsCollector metricsCollector;

    public AbstractFlatHashCacheAgent(MultiTierFlatHashCacheConfig config, FlatHashCacheLoader<F, V> cacheLoader,
                                      CacheAccessMetricsCollector metricsCollector) {
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.metricsCollector = metricsCollector;

        KeyConverter<Void> keyConverter = KeyConverterFactory.createDefault(config.getSpec().getKeyPrefix(),
                config.getSpec().getUseHashTag());
        this.cache = new DefaultMultiTierFlatHashCache<>(config.getSpec().getCacheName(),
                config, keyConverter, getFieldConverter());
    }

    @Override
    public V get(F field) {
        FlatHashAccessResult<CacheValueHolder<V>> result = getWithResult(field);
        return result != null && result.getValue() != null ? result.getValue().getValue() : null;
    }

    @Override
    public FlatHashAccessResult<CacheValueHolder<V>> getWithResult(F field) {
        try {
            return cache.getWithResult(field);
        } catch (Exception e) {
            log.warn("[{}] getWithResult() failed: {}", getName(), e.getMessage(), e);
            return FlatHashAccessResult.failGet(e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public Map<F, V> listAll() {
        FlatHashAccessResult<Map<F, CacheValueHolder<V>>> result = listAllWithResult();
        if (null != result.getTrace()) {
            log.debug("trace=" + result.getTrace());
        }
        if (null != result.getValue()) {
            Map<F, V> map = new HashMap<>();
            for (Map.Entry<F, CacheValueHolder<V>> entry : result.getValue().entrySet()) {
                map.put(entry.getKey(), entry.getValue().getValue());
            }
            return map;
        }
        log.warn("listAllWithResult() no cache found");
        return Collections.emptyMap();
    }

    @Override
    public FlatHashAccessResult<Map<F, CacheValueHolder<V>>> listAllWithResult() {
        try {
            return cache.listAllWithResult();
        } catch (Exception e) {
            log.warn("[{}] listAllWithResult() failed: {}", getName(), e.getMessage(), e);
            return FlatHashAccessResult.failRefresh(e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public void notifyDirty() {
        FlatHashAccessResult<Map<F, V>> result = refreshAllWithResult();
        if (result.isSuccess()) {
            log.debug("[{}] refresh success", getName());
        } else {
            log.warn("[{}] refresh failed, exception: {}", getName(), result.getException() != null
                    ? result.getException().getStackTrace() : "unknown");
        }
    }

    protected final FlatHashAccessResult<Map<F, V>> refreshAllWithResult() {
        long start = System.currentTimeMillis();
        try {
            Map<F, V> map = cacheLoader.loadAll();
            if (map == null || map.isEmpty()) {
                return FlatHashAccessResult.failRefresh(new IllegalStateException("Loaded config map is empty"));
            }
            FlatHashAccessResult<Void> putResult = cache.putAllWithResult(map);
            if (!putResult.isSuccess()) {
                return FlatHashAccessResult.failRefresh(putResult.getException());
            }
            return FlatHashAccessResult.success(map);
        } catch (Exception e) {
            log.warn("[{}] refresh failed: {}", getName(), e.getMessage(), e);
            return FlatHashAccessResult.failRefresh(e);
        } finally {
            CacheAccessContext.clear();
            long cost = System.currentTimeMillis() - start;
            log.debug("[{}] refresh cost={}ms", getName(), cost);
        }
    }

    protected abstract FieldConverter<F> getFieldConverter();

    public abstract String getName();
}
