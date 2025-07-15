package com.yetcache.agent.core.structure.dynamichash;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.agent.core.structure.AbstractCacheAgent;
import com.yetcache.agent.core.CacheValueHolderHelper;
import com.yetcache.agent.governance.plugin.MetricsInterceptor;
import com.yetcache.agent.governance.plugin.PenetrationProtectInterceptor;
import com.yetcache.agent.interceptor.CacheAccessKey;
import com.yetcache.agent.interceptor.CacheInvocationChain;
import com.yetcache.agent.interceptor.CacheInvocationContext;
import com.yetcache.agent.interceptor.DefaultInvocationChain;
import com.yetcache.agent.protect.CaffeinePenetrationProtector;
import com.yetcache.agent.protect.CompositePenetrationProtector;
import com.yetcache.agent.protect.PenetrationProtector;
import com.yetcache.agent.protect.RedisPenetrationProtector;
import com.yetcache.agent.protect.cache.CaffeinePenetrationProtectCache;
import com.yetcache.agent.result.DynamicHashCacheAgentResult;
import com.yetcache.core.cache.dynamichash.DefaultMultiTierDynamicHashCache;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.config.dynamichash.DynamicHashCacheEnhanceConfig;
import com.yetcache.core.config.dynamichash.DynamicHashCacheSpec;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.result.CacheOutcome;
import com.yetcache.core.result.StorageCacheAccessResult;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
public class AbstractDynamicHashCacheAgent<K, F, V> extends AbstractCacheAgent<DynamicHashCacheAgentResult<K, F, V>>
        implements DynamicHashCacheAgent<K, F, V> {
    protected final MultiTierDynamicHashCache<K, F, V> cache;
    protected final DynamicHashCacheConfig config;
    protected final DynamicHashCacheLoader<K, F, V> cacheLoader;
    private final Cache<K, Long> fullyLoadedTs;

    public AbstractDynamicHashCacheAgent(String componentNane,
                                         DynamicHashCacheConfig config,
                                         RedissonClient redissonClient,
                                         KeyConverter<K> keyConverter,
                                         FieldConverter<F> fieldConverter,
                                         DynamicHashCacheLoader<K, F, V> cacheLoader,
                                         MeterRegistry registry) {
        super(componentNane);
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.cache = new DefaultMultiTierDynamicHashCache<>(componentNane, config, redissonClient, keyConverter,
                fieldConverter);

        this.fullyLoadedTs = Caffeine.newBuilder()
                .expireAfterWrite(config.getSpec().getFullyLoadedExpireSecs(), TimeUnit.MINUTES)
                .maximumSize(100_000)
                .build();


        PenetrationProtectConfig localPpConfig = config.getEnhance().getLocalPenetrationProtect();
        CaffeinePenetrationProtector localProtector = CaffeinePenetrationProtector.of(localPpConfig.getPrefix()
                , getComponentName(), localPpConfig.getTtlSecs(), localPpConfig.getMaxSize());
        PenetrationProtectConfig remotePpConfig = config.getEnhance().getRemotePenetrationProtect();
        RedisPenetrationProtector redisProtector = RedisPenetrationProtector.of(redissonClient
                , remotePpConfig.getPrefix(), getComponentName(), remotePpConfig.getTtlSecs(),
                remotePpConfig.getMaxSize());

        PenetrationProtector penetrationProtector = new CompositePenetrationProtector(localProtector, redisProtector);
        boolean allowNullValue = config.getSpec().getAllowNullValue() != null
                ? config.getSpec().getAllowNullValue() : false;
        this.interceptors.add(new PenetrationProtectInterceptor(penetrationProtector, allowNullValue));
        this.interceptors.add(new MetricsInterceptor(registry));
    }

    protected void registerDefaultInterceptors(DynamicHashCacheSpec spec, DynamicHashCacheEnhanceConfig enhanceConfig,
                                               MeterRegistry registry) {

        if (registry != null) {
            this.interceptors.add(new MetricsInterceptor(registry));
        }
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> get(K bizKey, F bizField) {
        return invoke("get", () -> doGet(bizKey, bizField), new CacheAccessKey(bizKey, bizField));
    }

    protected DynamicHashCacheAgentResult<K, F, V> doGet(K bizKey, F bizField) {
        try {
            StorageCacheAccessResult<CacheValueHolder<V>> result = cache.get(bizKey, bizField);
            if (result.outcome() == CacheOutcome.HIT) {
                CacheValueHolder<V> holder = result.value();
                if (holder.isNotLogicExpired()) {
                    return DynamicHashCacheAgentResult.success(getComponentName(), Map.of(bizField, holder), result.getTier());
                }
            }

            // 回源加载数据
            V loaded = cacheLoader.load(bizKey, bizField);
            if (loaded == null) {
                return DynamicHashCacheAgentResult.dynamicHashNotFound(getComponentName());
            }

            // 封装为缓存值并写入缓存
            cache.put(bizKey, bizField, loaded);

            return DynamicHashCacheAgentResult.success(getComponentName(),
                    Map.of(bizField, CacheValueHolder.wrap(loaded, 0)), HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return DynamicHashCacheAgentResult.dynamicHashFail(componentName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> listAll(K bizKey) {
        boolean force = CacheAccessContext.isForceRefresh();
        return invoke("listAll", () -> loadAllAndUpdate(bizKey, force), new CacheAccessKey(bizKey, null));
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> refreshAll(K bizKey) {
        return invoke("refreshAll", () -> loadAllAndUpdate(bizKey, true));
    }

    private DynamicHashCacheAgentResult<K, F, V> loadAllAndUpdate(K bizKey, boolean force) {
        try {
            // ✅ 1. 非强制刷新时，优先使用结构新鲜期窗口逻辑
            if (!force && withinFullyLoadedExpireWindow(bizKey)) {
                log.debug("Fully-loaded freshness window active for key = {}", bizKey);
                StorageCacheAccessResult<Map<F, CacheValueHolder<V>>> result = cache.listAll(bizKey);
                if (result.outcome() == CacheOutcome.HIT) {
                    return DynamicHashCacheAgentResult.success(
                            getComponentName(), result.value(), result.getTier());
                }
            }

            // ✅ 2. 回源加载数据
            Map<F, V> loadedMap = cacheLoader.loadAll(bizKey);
            if (loadedMap == null || loadedMap.isEmpty()) {
                return DynamicHashCacheAgentResult.dynamicHashNotFound(getComponentName());
            }

            // ✅ 3. 回源成功 → 缓存 + 更新结构级 fullyLoadedTs 标记
            cache.putAll(bizKey, loadedMap);
            fullyLoadedTs.put(bizKey, System.currentTimeMillis());

            return DynamicHashCacheAgentResult.success(
                    getComponentName(),
                    CacheValueHolderHelper.wrapAsHolderMap(loadedMap),
                    HitTier.SOURCE);

        } catch (Exception e) {
            log.warn("cache loadAll failed, agent = {}, key = {}", componentName, bizKey, e);
            return DynamicHashCacheAgentResult.dynamicHashFail(componentName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    private boolean withinFullyLoadedExpireWindow(K bizKey) {
        long expireSecs = config.getSpec().getFullyLoadedExpireSecs();
        if (expireSecs <= 0) return false;

        Long lastFullLoad = fullyLoadedTs.getIfPresent(bizKey);
        return lastFullLoad != null &&
                (System.currentTimeMillis() - lastFullLoad <= expireSecs * 1000L);
    }


    @Override
    public DynamicHashCacheAgentResult<K, F, V> invalidate(K bizKey, F bizField) {
        return invoke("invalidate", () -> doInvalidate(bizKey, bizField));
    }

    private DynamicHashCacheAgentResult<K, F, V> doInvalidate(K bizKey, F bizField) {
        try {
            cache.invalidate(bizKey, bizField);
            return DynamicHashCacheAgentResult.success(getComponentName(),
                    Collections.emptyMap(), HitTier.NONE); // 删除不关心命中层
        } catch (Exception e) {
            log.warn("invalidate failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return DynamicHashCacheAgentResult.dynamicHashFail(componentName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> invalidateAll(K bizKey) {
        return invoke("invalidateAll", () -> doInvalidateAll(bizKey));
    }

    private DynamicHashCacheAgentResult<K, F, V> doInvalidateAll(K bizKey) {
        try {
            cache.invalidateAll(bizKey);
            return DynamicHashCacheAgentResult.success(getComponentName(),
                    Collections.emptyMap(), HitTier.NONE);
        } catch (Exception e) {
            log.warn("invalidateAll failed, agent = {}, key = {}", componentName, bizKey, e);
            return DynamicHashCacheAgentResult.dynamicHashFail(componentName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DynamicHashCacheAgentResult<K, F, V> defaultFail(String method, Throwable t) {
        return DynamicHashCacheAgentResult.dynamicHashFail(componentName, t);
    }
}
