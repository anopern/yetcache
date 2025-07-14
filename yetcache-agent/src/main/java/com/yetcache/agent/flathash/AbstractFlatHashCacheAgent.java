package com.yetcache.agent.flathash;

import com.yetcache.agent.ForceIntervalRefreshable;
import com.yetcache.agent.MetricsInterceptor;
import com.yetcache.agent.interceptor.CacheInvocationChain;
import com.yetcache.agent.interceptor.CacheInvocationContext;
import com.yetcache.agent.interceptor.CacheInvocationInterceptor;
import com.yetcache.agent.interceptor.DefaultInvocationChain;
import com.yetcache.agent.preload.MandatoryStartupInitializable;
import com.yetcache.agent.result.FlatHashCacheAgentResult;
import com.yetcache.core.cache.flathash.*;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.result.CacheAccessResult;
import com.yetcache.core.result.CacheOutcome;
import com.yetcache.core.result.FlatHashStorageResult;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.key.KeyConverterFactory;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 基于 FlatHashCacheAgentResult 的聚合层基类，实现统一的 trace / outcome / metrics 处理。
 * <p>
 * 只暴露 get / listAll / notifyDirty 三个高阶 API，禁止字段级刷新。
 * 运行期不回源，失败场景通过 CacheAgentResult.outcome() 上报。
 * </p>
 *
 * @author walter.yan
 * @since 2025/07/13
 */
@Slf4j
public abstract class AbstractFlatHashCacheAgent<F, V> implements FlatHashCacheAgent<F, V>
        , MandatoryStartupInitializable, ForceIntervalRefreshable {
    protected final String componentNane;
    protected final MultiTierFlatHashCache<F, V> cache;
    protected final MultiTierFlatHashCacheConfig config;
    protected final FlatHashCacheLoader<F, V> cacheLoader;
    /**
     * 并发刷新防抖
     */
    private final AtomicBoolean refreshing = new AtomicBoolean(false);
    /**
     * 拦截器链，可按需在子类或外部继续追加
     */
    private final List<CacheInvocationInterceptor> interceptors = new CopyOnWriteArrayList<>();

    /* ---------------- ctor ---------------- */
    public AbstractFlatHashCacheAgent(String componentNane,
                                      MultiTierFlatHashCacheConfig config,
                                      FlatHashCacheLoader<F, V> cacheLoader,
                                      MeterRegistry registry) {
        this.componentNane = componentNane;
        this.config = config;
        this.cacheLoader = cacheLoader;

        KeyConverter<Void> keyConverter = KeyConverterFactory.createDefault(
                config.getSpec().getKeyPrefix(),
                config.getSpec().getUseHashTag());
        this.cache = new DefaultMultiTierFlatHashCache<>(
                config.getSpec().getCacheName(),
                config, keyConverter, getFieldConverter());

        // 默认仅注入指标拦截器；可通过 getInterceptors().add(...) 再拼装
        this.interceptors.add(new MetricsInterceptor(registry));
    }

    @SuppressWarnings("unchecked")
    private <R extends CacheAccessResult<?>> R invoke(String method,
                                                      Supplier<R> business) {
        CacheInvocationContext ctx = CacheInvocationContext.start(componentNane, method);
        CacheInvocationChain<R> chain = new DefaultInvocationChain<>(interceptors, business);
        try {
            return chain.proceed(ctx);               // R 已经是目标类型
        } catch (Throwable t) {
            log.error("[{}] {} failed", componentNane, method, t);
            return (R) FlatHashCacheAgentResult.flatHashFail(componentNane, t);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public FlatHashCacheAgentResult<F, V> initialize() {
        return invoke("initialize", this::doRefreshAllInternal);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FlatHashCacheAgentResult<F, V> intervalRefresh() {
        return invoke("intervalRefresh", this::doRefreshAllInternal);
    }

    @Override
    public FlatHashCacheAgentResult<F, V> listAll() {
        return invoke("listAll",
                () -> convertToAgentResult(cache.listAll()));
    }

    /* ====================================================================== */
    /*                              GOVERNANCE                                */
    /* ====================================================================== */

    @Override
    public void notifyDirty() {
        FlatHashCacheAgentResult<F, V> res = refreshAllInternal();
        if (!res.isSuccess()) {
            log.warn("[{}] refresh failed: {}", componentNane, res.trace().reason());
        }
    }

    /**
     * 仅供运维 / 定时器调用，执行全量刷新。
     */
    protected FlatHashCacheAgentResult<F, V> refreshAllInternal() {
        return invoke("refreshAllInternal", this::doRefreshAllInternal);
    }

    protected FlatHashCacheAgentResult<F, V> doRefreshAllInternal() {
        try {
            Map<F, V> data = cacheLoader.loadAll();
            if (data == null || data.isEmpty()) {
                return FlatHashCacheAgentResult.flatHashFail(componentNane, new IllegalStateException("Loaded map empty"));
            }
            FlatHashStorageResult<F, V> putRes = cache.putAll(data);
            if (!putRes.outcome().equals(CacheOutcome.SUCCESS)) {
                return FlatHashCacheAgentResult.flatHashFail(componentNane, new RuntimeException("putAll failed"));
            }
            Map<F, CacheValueHolder<V>> wrapped = new HashMap<>();
            data.forEach((f, v) -> wrapped.put(f, CacheValueHolder.wrap(v, config.getLocal().getTtlSecs())));
            return FlatHashCacheAgentResult.success(componentNane, wrapped, false);
        } catch (Exception e) {
            return FlatHashCacheAgentResult.flatHashFail(componentNane, e);
        }
    }

    /* ====================================================================== */
    /*                              HELPERS                                   */
    /* ====================================================================== */

    private FlatHashCacheAgentResult<F, V> convertToAgentResult(FlatHashStorageResult<F, V> raw) {
        switch (raw.outcome()) {
            case HIT:
            case SUCCESS:
                return FlatHashCacheAgentResult.success(componentNane, raw.value(), true);
            case MISS:
                return FlatHashCacheAgentResult.flatHashMiss(componentNane);
            case BLOCK:
                return FlatHashCacheAgentResult.flatHashBlock(componentNane, raw.trace().reason());
            default:
                return FlatHashCacheAgentResult.flatHashFail(componentNane, raw.trace().exception());
        }
    }

    protected abstract FieldConverter<F> getFieldConverter();

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getComponentName() {
        return this.componentNane;
    }

    @Override
    public long getRefreshIntervalSecs() {
        return config.getSpec().getRefreshIntervalSecs();
    }
}
