package com.yetcache.agent.core.structure.dynamichash;

import cn.hutool.core.collection.CollUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.agent.core.structure.AbstractCacheAgent;
import com.yetcache.agent.core.CacheValueHolderHelper;
import com.yetcache.agent.governance.plugin.MetricsInterceptor;
import com.yetcache.agent.interceptor.CacheAccessKey;
import com.yetcache.agent.interceptor.CacheInvocationInterceptor;
import com.yetcache.agent.result.DynamicHashCacheAgentSingleAccessResult;
import com.yetcache.core.cache.dynamichash.DefaultMultiTierDynamicHashCache;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.config.dynamichash.DynamicHashCacheEnhanceConfig;
import com.yetcache.core.config.dynamichash.DynamicHashCacheSpec;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.result.CacheOutcome;
import com.yetcache.core.result.DynamicCacheStorageSingleAccessResult;
import com.yetcache.core.result.StorageCacheAccessResultBak;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
public class AbstractDynamicHashCacheAgent<K, F, V> extends AbstractCacheAgent<DynamicHashCacheAgentSingleAccessResult<K, F, V>>
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
                                         List<CacheInvocationInterceptor> interceptors) {
        super(componentNane);
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.cache = new DefaultMultiTierDynamicHashCache<>(componentNane, config, redissonClient, keyConverter,
                fieldConverter);

        this.fullyLoadedTs = Caffeine.newBuilder()
                .expireAfterWrite(config.getSpec().getFullyLoadedExpireSecs(), TimeUnit.MINUTES)
                .maximumSize(100_000)
                .build();


//        PenetrationProtectConfig localPpConfig = config.getEnhance().getLocalPenetrationProtect();
//        CaffeinePenetrationProtector localProtector = CaffeinePenetrationProtector.of(localPpConfig.getPrefix()
//                , getComponentName(), localPpConfig.getTtlSecs(), localPpConfig.getMaxSize());
//        PenetrationProtectConfig remotePpConfig = config.getEnhance().getRemotePenetrationProtect();
//        RedisPenetrationProtector redisProtector = RedisPenetrationProtector.of(redissonClient
//                , remotePpConfig.getPrefix(), getComponentName(), remotePpConfig.getTtlSecs(),
//                remotePpConfig.getMaxSize());
//
//        PenetrationProtector penetrationProtector = new CompositePenetrationProtector(localProtector, redisProtector);
//        boolean allowNullValue = config.getSpec().getAllowNullValue() != null
//                ? config.getSpec().getAllowNullValue() : false;
//        this.interceptors.add(new MetricsInterceptor(registry));

        if (CollUtil.isNotEmpty(interceptors)) {
            this.interceptors.addAll(interceptors);
        }
    }

    protected void registerDefaultInterceptors(DynamicHashCacheSpec spec, DynamicHashCacheEnhanceConfig enhanceConfig,
                                               MeterRegistry registry) {

        if (registry != null) {
            this.interceptors.add(new MetricsInterceptor(registry));
        }
    }

    @Override
    public DynamicHashCacheAgentSingleAccessResult<V> get(K bizKey, F bizField) {
        return invoke("get", () -> doGet(bizKey, bizField), new CacheAccessKey(bizKey, bizField));
    }

    protected DynamicHashCacheAgentSingleAccessResult<V> doGet(K bizKey, F bizField) {
        try {
            DynamicCacheStorageSingleAccessResult<V> result = cache.get(bizKey, bizField);
            if (result.outcome() == CacheOutcome.HIT) {
                CacheValueHolder<V> holder = result.value();
                if (holder.isNotLogicExpired()) {
                    return DynamicHashCacheAgentSingleAccessResult.hit(getCacheName(), holder, result.getHitTier());
                }
            }

            // 回源加载数据
            V loaded = cacheLoader.load(bizKey, bizField);
            if (loaded == null) {
                return DynamicHashCacheAgentSingleAccessResult.notFound(getCacheName());
            }

            // 封装为缓存值并写入缓存
            cache.put(bizKey, bizField, loaded);

            return DynamicHashCacheAgentSingleAccessResult.hit(getCacheName(), CacheValueHolder.wrap(loaded, 0),
                    HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", cacheName, bizKey, bizField, e);
            return DynamicHashCacheAgentSingleAccessResult.fail(cacheName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public DynamicHashCacheAgentSingleAccessResult<K, F, V> batchGet(K bizKey, List<F> bizFields) {
        return invoke("batchGet", () -> doBatchGet(bizKey, bizFields), CacheAccessKey.batch(bizKey, bizFields));
    }

    private DynamicHashCacheAgentSingleAccessResult<K, F, V> doBatchGet(K bizKey, List<F> bizFields) {
        Map<F, StorageCacheAccessResultBak<CacheValueHolder<V>>> resultMap = new HashMap<>();
        HitTier finalTier = HitTier.MIXED;

        try {
            // Step 1: 批量从缓存获取
            Map<F, StorageCacheAccessResultBak<CacheValueHolder<V>>> cacheResult = cache.batchGet(bizKey, bizFields);

            // Step 2: 识别需要回源的字段
            List<F> missedFields = new ArrayList<>();
            for (Map.Entry<F, StorageCacheAccessResultBak<CacheValueHolder<V>>> entry : cacheResult.entrySet()) {
                StorageCacheAccessResultBak<CacheValueHolder<V>> access = entry.getValue();
                if (access.outcome() == CacheOutcome.HIT && access.value().isNotLogicExpired()) {
                    resultMap.put(entry.getKey(), access); // 命中直接返回
                } else {
                    missedFields.add(entry.getKey());      // miss 或过期，需回源
                }
            }

            // Step 3: 回源加载 + 回写缓存
            if (!missedFields.isEmpty()) {
                Map<F, V> loadedMap = cacheLoader.batchLoad(bizKey, missedFields);
                for (F field : missedFields) {
                    V loaded = loadedMap.get(field);
                    if (loaded != null) {
                        resultMap.put(field, StorageCacheAccessResultBak.hit(CacheValueHolder.wrap(loaded, 0),
                                HitTier.SOURCE));
                    } else {
                        resultMap.put(field, StorageCacheAccessResultBak.notFound());
                    }
                }

                if (!toPut.isEmpty()) {
                    cache.putAll(bizKey, toPut); // 一次性批量写入缓存
                }
            }

            return resultMap;
        } catch (Exception e) {
            log.warn("batchGet with fallback failed, agent = {}, key = {}, fields = {}", cacheName, bizKey, bizFields, e);
            // 全部失败返回 fail 结果
            Map<F, StorageCacheAccessResultBak<CacheValueHolder<V>>> failResult = new HashMap<>();
            for (F field : bizFields) {
                failResult.put(field, StorageCacheAccessResultBak.fail(e));
            }
            return failResult;
        } finally {
            CacheAccessContext.clear();
        }
    }


    @Override
    public DynamicHashCacheAgentSingleAccessResult<K, F, V> listAll(K bizKey) {
        boolean force = CacheAccessContext.isForceRefresh();
        return invoke("listAll", () -> loadAllAndUpdate(bizKey, force), new CacheAccessKey(bizKey, null));
    }

    @Override
    public DynamicHashCacheAgentSingleAccessResult<K, F, V> batchRefresh(Map<K, List<F>> bizKeyMap) {
        return invoke("batchRefresh", () -> doBatchRefresh(bizKeyMap), CacheAccessKey.batch(bizKeyMap));
    }

    public DynamicHashCacheAgentSingleAccessResult<K, F, V> doBatchRefresh(Map<K, List<F>> bizKeyMap) {
        Map<F, CacheValueHolder<V>> resultMap = new HashMap<>();

        try {
            for (Map.Entry<K, List<F>> entry : bizKeyMap.entrySet()) {
                K key = entry.getKey();
                List<F> fields = entry.getValue();

                for (F field : fields) {
                    try {
                        V loaded = cacheLoader.load(key, field);
                        if (loaded != null) {
                            CacheValueHolder<V> holder = CacheValueHolder.wrap(loaded, 0);
                            cache.put(key, field, loaded);
                            resultMap.put(field, holder);
                        } else {
                            // source 为空，可选是否 invalidate（此处不处理）
                            log.debug("batchRefresh skip null load, key = {}, field = {}", key, field);
                        }
                    } catch (Exception e) {
                        log.warn("batchRefresh failed for key = {}, field = {}", key, field, e);
                    }
                }
            }

            if (resultMap.isEmpty()) {
                return DynamicHashCacheAgentSingleAccessResult.dynamicHashNotFound(cacheName);
            }

            return DynamicHashCacheAgentSingleAccessResult.success(cacheName, resultMap, HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("batchRefresh failed, agent = {}", cacheName, e);
            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public DynamicHashCacheAgentSingleAccessResult<K, F, V> refreshAll(K bizKey) {
        return invoke("refreshAll", () -> loadAllAndUpdate(bizKey, true));
    }

    private DynamicHashCacheAgentSingleAccessResult<K, F, V> loadAllAndUpdate(K bizKey, boolean force) {
        try {
            // ✅ 1. 非强制刷新时，优先使用结构新鲜期窗口逻辑
            if (!force && withinFullyLoadedExpireWindow(bizKey)) {
                log.debug("Fully-loaded freshness window active for key = {}", bizKey);
                StorageCacheAccessResultBak<Map<F, CacheValueHolder<V>>> result = cache.listAll(bizKey);
                if (result.outcome() == CacheOutcome.HIT) {
                    return DynamicHashCacheAgentSingleAccessResult.success(
                            getCacheName(), result.value(), result.getTier());
                }
            }

            // ✅ 2. 回源加载数据
            Map<F, V> loadedMap = cacheLoader.loadAll(bizKey);
            if (loadedMap == null || loadedMap.isEmpty()) {
                // ✅ 如果 source 明确为空，删除缓存，防止污染
                cache.invalidateAll(bizKey);
                fullyLoadedTs.invalidate(bizKey);
                log.info("force refresh removed empty structure, agent = {}, key = {}", cacheName, bizKey);
                return DynamicHashCacheAgentSingleAccessResult.dynamicHashNotFound(getCacheName());
            }

            // ✅ 3. 回源成功 → 缓存 + 更新结构级 fullyLoadedTs 标记
            cache.putAll(bizKey, loadedMap);
            fullyLoadedTs.put(bizKey, System.currentTimeMillis());

            return DynamicHashCacheAgentSingleAccessResult.success(
                    getCacheName(),
                    CacheValueHolderHelper.wrapAsHolderMap(loadedMap),
                    HitTier.SOURCE);

        } catch (Exception e) {
            log.warn("cache loadAll failed, agent = {}, key = {}", cacheName, bizKey, e);
            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    private boolean withinFullyLoadedExpireWindow(K bizKey) {
        long expireSecs = config.getSpec().getFullyLoadedExpireSecs();
        if (expireSecs <= 0) {
            return false;
        }

        Long lastFullLoad = fullyLoadedTs.getIfPresent(bizKey);
        return lastFullLoad != null &&
                (System.currentTimeMillis() - lastFullLoad <= expireSecs * 1000L);
    }


    @Override
    public DynamicHashCacheAgentSingleAccessResult<K, F, V> remove(K bizKey, F bizField) {
        return invoke("invalidate", () -> doInvalidate(bizKey, bizField));
    }

    private DynamicHashCacheAgentSingleAccessResult<K, F, V> doInvalidate(K bizKey, F bizField) {
        try {
            cache.invalidate(bizKey, bizField);
            return DynamicHashCacheAgentSingleAccessResult.success(getCacheName(),
                    Collections.emptyMap(), HitTier.NONE); // 删除不关心命中层
        } catch (Exception e) {
            log.warn("invalidate failed, agent = {}, key = {}, field = {}", cacheName, bizKey, bizField, e);
            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public DynamicHashCacheAgentSingleAccessResult<K, F, V> removeAll(K bizKey) {
        return invoke("invalidateAll", () -> doInvalidateAll(bizKey));
    }

    @Override
    public DynamicHashCacheAgentSingleAccessResult<K, F, V> put(K bizKey, F bizField, V value) {
        return invoke("put", () -> doPut(bizKey, bizField, value), new CacheAccessKey(bizKey, bizField));
    }

    public DynamicHashCacheAgentSingleAccessResult<K, F, V> doPut(K bizKey, F bizField, V value) {
        if (bizKey == null || bizField == null || value == null) {
            return DynamicHashCacheAgentSingleAccessResult.badParam(cacheName);
        }
        try {
            cache.put(bizKey, bizField, value);
            return DynamicHashCacheAgentSingleAccessResult.success(cacheName);
        } catch (Exception e) {
            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
        }
    }

    @Override
    public DynamicHashCacheAgentSingleAccessResult<K, F, V> putAll(K bizKey, Map<F, V> valueMap) {
        Map<K, List<F>> bizKeyMap = valueMap.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new ArrayList<>(e.getValue().keySet())
                ));
        return invoke("putAll", () -> doPutAll(bizKey, valueMap), CacheAccessKey.batch(bizKeyMap));
    }

    public DynamicHashCacheAgentSingleAccessResult<K, F, V> doPutAll(K bizKey, Map<F, V> valueMap) {
        if (valueMap == null || valueMap.isEmpty()) {
            return DynamicHashCacheAgentSingleAccessResult.badParam(cacheName);
        }

        try {
            for (Map.Entry<K, Map<F, V>> entry : valueMap.entrySet()) {
                cache.putAll(entry.getKey(), entry.getValue());
            }
            return DynamicHashCacheAgentSingleAccessResult.success(cacheName);
        } catch (Exception e) {
            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
        }
    }


    private DynamicHashCacheAgentSingleAccessResult<K, F, V> doInvalidateAll(K bizKey) {
        try {
            cache.invalidateAll(bizKey);
            return DynamicHashCacheAgentSingleAccessResult.success(getCacheName(),
                    Collections.emptyMap(), HitTier.NONE);
        } catch (Exception e) {
            log.warn("invalidateAll failed, agent = {}, key = {}", cacheName, bizKey, e);
            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DynamicHashCacheAgentSingleAccessResult<K, F, V> defaultFail(String method, Throwable t) {
        return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, t);
    }
}
