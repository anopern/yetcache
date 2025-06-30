package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.AbstractMultiTierCache;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.cache.result.CacheResult;
import com.yetcache.core.cache.result.singlehash.FlatHashCacheGetResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.singlehash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.field.CacheFieldConverter;
import com.yetcache.core.support.key.CacheKeyConverter;
import com.yetcache.core.support.result.CacheLoadResult;
import com.yetcache.core.support.result.CacheLookupResult;
import com.yetcache.core.support.trace.CacheAccessRecorder;
import com.yetcache.core.support.trace.DefaultCacheAccessRecorder;
import com.yetcache.core.support.util.CacheParamChecker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.*;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class MultiTierFlatHashCache<K, F, V> extends AbstractMultiTierCache<F>
        implements FlatHashCache<K, F, V> {
    private String cacheName;
    private final MultiTierFlatHashCacheConfig config;
    private final FlatHashCacheLoader<K, F, V> cacheLoader;
    private CaffeineFlatHashCache<V> localCache;
    private RedisFlatHashCache<V> remoteCache;
    private CacheKeyConverter<K> keyConverter;
    private CacheFieldConverter<F> fieldConverter;

    public MultiTierFlatHashCache(String cacheName,
                                  MultiTierFlatHashCacheConfig config,
                                  RedissonClient rClient,
                                  FlatHashCacheLoader<K, F, V> cacheLoader,
                                  CacheKeyConverter<K> keyConverter,
                                  CacheFieldConverter<F> fieldConverter) {
        this.cacheName = cacheName;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.keyConverter = keyConverter;
        this.fieldConverter = fieldConverter;

        if (config.getCacheTier().useLocal()) {
            config.getLocal().setTtlRandomPercent(config.getTtlRandomPercent());
            this.localCache = new CaffeineFlatHashCache<>(config.getLocal());

            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache<>(ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (config.getCacheTier().useRemote()) {
            config.getRemote().setTtlRandomPercent(config.getTtlRandomPercent());
            this.remoteCache = new RedisFlatHashCache<>(config.getRemote(), rClient);

            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
            this.remotePpCache = new RedisPenetrationProtectCache<>(rClient, ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }
    }

    @Override
    public V get(K bizKey, F bizField) {
        CacheResult<K, F, V> result = getWithResult(bizKey, bizField);
        return result != null ? result.getValueHolder().getValue() : null;
    }

    @Override
    public CacheResult<K, F, V> getWithResult(K bizKey, F bizField) {
        try {
            CacheParamChecker.failIfNull(bizField, cacheName);
            DefaultCacheAccessRecorder<K, F> recorder = new DefaultCacheAccessRecorder<>();
            recorder.onStart(bizKey, bizField);
            String key = keyConverter.convert(bizKey);
            String field = fieldConverter.convert(bizField);
            CacheResult<K, F, V> result = new CacheResult<>(CacheAccessContext.getTrace());

            if (localPpCache != null && localPpCache.isBlocked(bizField)) {
                recorder.localBlocked(bizField);
                recorder.end();
                result.setTrace(CacheAccessContext.getTrace());
                return result;
            }

            if (remotePpCache != null && remotePpCache.isBlocked(bizField)) {
                recorder.remoteBlocked(bizField);
                recorder.end();
                result.setTrace(CacheAccessContext.getTrace());
                return result;
            }

            CacheLookupResult<V> localResult = tryLocalGet(key, field);
            if (localResult != null) {
                if (localResult.isHit()) {
                    recorder.localHit(bizField);
                    result.setValueHolder(localResult.getValueHolder());
                } else if (localResult.isPhysicalMiss()) {
                    recorder.localPhysicalMiss(bizField);
                } else if (localResult.isLogicalExpired()) {
                    recorder.localLogicExpired(bizField);
                }
            }

            CacheLookupResult<V> remoteResult = tryRemoteGet(key, field);
            if (remoteResult != null) {
                if (remoteResult.isHit()) {
                    recorder.remoteHit(bizField);
                    result.setValueHolder(remoteResult.getValueHolder());
                } else if (remoteResult.isPhysicalMiss()) {
                    recorder.remotePhysicalMiss(bizField);
                } else if (remoteResult.isLogicalExpired()) {
                    recorder.remoteLogicExpired(bizField);
                }
            }

            if (!Boolean.TRUE.equals(config.getEnableLoadFallbackOnMiss())) {
                recorder.end();
                return result;
            }

            CacheLoadResult<V> loadResult = tryLoad(bizKey, bizField);
            if (loadResult.isLoaded()) {
                recorder.sourceLoaded(bizField);
            } else if (loadResult.isNoValue()) {
                recorder.sourceLoadNoValue(bizField);
            } else if (loadResult.isError()) {
                recorder.sourceLoadError(bizField);
            }
            recorder.end();
            return result;
        } finally {
            CacheAccessContext.clear();
        }
    }

    private void recordLocal(CacheAccessRecorder<K, F> recorder, CacheLookupResult<CacheValueHolder<V>> result) {

    }

//    @Override
//    public FlatHashCacheGetResult<F, V> batGetWithResult(Collection<F> bizFields) {
//        try {
//            CacheAccessContext.setSourceNormal();
//            String key = config.getKey();
//            FlatHashCacheGetResult<F, V> result = new FlatHashCacheGetResult<>(cacheName, config.getCacheTier(), key,
//                    System.currentTimeMillis());
//            Map<F, String> fieldMap = new HashMap<>();
//            for (F bizField : bizFields) {
//                CacheParamChecker.failIfNull(bizField, cacheName);
//                fieldMap.put(bizField, fieldConverter.convert(bizField));
//            }
//            // 1. 从 local 批量获取
//            Map<String, CacheValueHolder<V>> localValueHolderMap = localCache != null
//                    ? localCache.batchGetIfPresent(key, fieldMap.values())
//                    : Collections.emptyMap();
//            Set<F> localMissBizKeys = new HashSet<>();
//            for (Map.Entry<F, String> entry : fieldMap.entrySet()) {
//                CacheValueHolder<V> holder = localValueHolderMap.get(entry.getValue());
//                if (holder != null && holder.isNotLogicExpired()) {
//                    result.recordLocalStatus(entry.getKey(), CacheAccessStatus.HIT);
//                } else {
//                    localMissBizKeys.add(entry.getKey());
//                }
//            }
//            if (localMissBizKeys.isEmpty()) {
//                return remapToBizKey(localValueHolderMap, fieldMap);
//            }
//        } finally {
//            CacheAccessContext.clear();
//        }
//        return null;
//    }

    public Map<F, CacheValueHolder<V>> remapToBizKey(Map<String, CacheValueHolder<V>> fieldMap) {
        Map<F, CacheValueHolder<V>> result = new HashMap<>();
        for (Map.Entry<String, CacheValueHolder<V>> entry : fieldMap.entrySet()) {
            result.put(fieldConverter.reverse(entry.getKey()), entry.getValue());
        }
        return result;
    }

    private boolean tryBlock(F bizField, FlatHashCacheGetResult<F, V> result) {
        return tryLocalBlock(bizField, result) || tryRemoteBlock(bizField, result);
    }

    private CacheLookupResult<V> tryLocalGet(String key, String field) {
        if (localCache == null) {
            return null;
        }
        CacheLookupResult<V> result = new CacheLookupResult<>();
        CacheValueHolder<V> holder = localCache.getIfPresent(key, field);
        if (holder == null) {
            result.physicalMiss();
        } else {
            if (holder.isNotLogicExpired()) {
                result.hit();
                result.setValueHolder(holder);
            } else {
                result.logicalExpired();
            }
        }
        return result;
    }

    private CacheLookupResult<V> tryRemoteGet(String key, String field) {
        if (remoteCache == null) {
            return null;
        }
        CacheLookupResult<V> result = new CacheLookupResult<>();
        CacheValueHolder<V> holder = remoteCache.getIfPresent(key, field);
        if (holder == null) {
            result.physicalMiss();
        } else {
            if (holder.isNotLogicExpired()) {
                result.hit();
                result.setValueHolder(holder);
            } else {
                result.logicalExpired();
            }
        }
        return result;
    }

    private CacheLoadResult<V> tryLoad(K bizKey, F bizField) {
        CacheLoadResult<V> result = new CacheLoadResult<>();
        try {
            V loaded = cacheLoader.load(bizKey, bizField);
            if (loaded == null) {
                result.noValue();
                markPenetrationProtect(bizField);
                return result;
            }

            CacheValueHolder<V> wrappedRemote = CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs());
            CacheValueHolder<V> wrappedLocal = CacheValueHolder.wrap(loaded, config.getLocal().getTtlSecs());

            String key = keyConverter.convert(bizKey);
            String field = fieldConverter.convert(bizField);
            if (remoteCache != null) {
                remoteCache.put(key, field, wrappedRemote);
            }
            if (localCache != null) {
                localCache.put(key, field, wrappedLocal);
            }
            result.loaded();
            result.setValueHolder(wrappedLocal);
        } catch (Exception e) {
            log.warn("缓存回源加载失败，cacheName={}, bizKey={}, bizField={}", cacheName, bizKey, bizField, e);
            result.error();
            result.setException(e);
        }
        return result;
    }

    private void markPenetrationProtect(F bizField) {
        if (localPpCache != null) {
            localPpCache.markMiss(bizField);
        }
        if (remotePpCache != null) {
            remotePpCache.markMiss(bizField);
        }
    }

    private FlatHashCacheGetResult<F, V> end(FlatHashCacheGetResult<F, V> result, CacheValueHolder<V> holder) {
        if (holder != null) {
            result.setValueHolder(holder);
        }
        result.end();
        return result;
    }

}
