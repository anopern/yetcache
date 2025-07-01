package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.AbstractMultiTierCache;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.cache.result.CacheResult;
import com.yetcache.core.cache.result.kv.KVCacheGetResult;
import com.yetcache.core.cache.result.singlehash.FlatHashCacheGetResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.singlehash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.key.NoneBizKeyKeyConverter;
import com.yetcache.core.support.result.CacheLoadResult;
import com.yetcache.core.support.result.CacheLookupResult;
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
    private KeyConverter<K> keyConverter;
    private FieldConverter<F> fieldConverter;

    public MultiTierFlatHashCache(String cacheName,
                                  MultiTierFlatHashCacheConfig config,
                                  RedissonClient rClient,
                                  FlatHashCacheLoader<K, F, V> cacheLoader,
                                  KeyConverter<K> keyConverter,
                                  FieldConverter<F> fieldConverter) {
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
    public V get(F bizField) {
        if (!(keyConverter instanceof NoneBizKeyKeyConverter)) {
            throw new IllegalArgumentException("Cache key must be not null");
        }
        return get(null, bizField);
    }

    @Override
    public V get(K bizKey, F bizField) {
        CacheResult<K, F, V> result = getWithResult(bizKey, bizField);
        log.debug("CacheResult: {}", result);
        if (result.getValueHolder() != null) {
            return result.getValueHolder().getValue();
        }
        return null;
    }

    @Override
    public CacheResult<K, F, V> getWithResult(K bizKey, F bizField) {
        try {
            if (!(keyConverter instanceof NoneBizKeyKeyConverter)) {
                CacheParamChecker.failIfNull(bizField, cacheName);
            }
            DefaultCacheAccessRecorder<K, F> recorder = new DefaultCacheAccessRecorder<>();
            recorder.onStart(bizKey, bizField);

            String key = keyConverter.convert(bizKey);
            String field = fieldConverter.convert(bizField);
            CacheResult<K, F, V> result = new CacheResult<>();

            // --- 封装后的三段核心逻辑 ---
            if (tryBlockAndRecord(bizField, recorder)) {
                return end(recorder, result);
            }

            if (tryCacheLookupAndRecord(key, field, bizField, recorder, result)) {
                return end(recorder, result);
            }

            if (Boolean.TRUE.equals(config.getEnableLoadFallbackOnMiss())) {
                tryLoadAndRecord(bizKey, bizField, key, field, recorder, result);
            }

            return end(recorder, result);
        } finally {
            CacheAccessContext.clear();
        }
    }

    private boolean tryBlockAndRecord(F bizField, DefaultCacheAccessRecorder<K, F> recorder) {
        if (localPpCache != null && localPpCache.isBlocked(bizField)) {
            recorder.localBlocked(bizField);
            return true;
        }
        if (remotePpCache != null && remotePpCache.isBlocked(bizField)) {
            recorder.remoteBlocked(bizField);
            return true;
        }
        return false;
    }

    private boolean tryCacheLookupAndRecord(String key, String field, F bizField,
                                            DefaultCacheAccessRecorder<K, F> recorder,
                                            CacheResult<K, F, V> result) {
        CacheLookupResult<V> localResult = tryLocalGet(key, field);
        if (localResult != null) {
            if (localResult.isHit()) {
                recorder.localHit(bizField);
                result.setValueHolder(localResult.getValueHolder());
                return true;
            } else if (localResult.isLogicalExpired()) {
                recorder.localLogicExpired(bizField);
            } else {
                recorder.localPhysicalMiss(bizField);
            }
        }

        CacheLookupResult<V> remoteResult = tryRemoteGet(key, field);
        if (remoteResult != null) {
            if (remoteResult.isHit()) {
                recorder.remoteHit(bizField);
                result.setValueHolder(remoteResult.getValueHolder());
                return true;
            } else if (remoteResult.isLogicalExpired()) {
                recorder.remoteLogicExpired(bizField);
            } else {
                recorder.remotePhysicalMiss(bizField);
            }
        }

        return false;
    }

    private void tryLoadAndRecord(K bizKey, F bizField, String key, String field,
                                  DefaultCacheAccessRecorder<K, F> recorder,
                                  CacheResult<K, F, V> result) {
        CacheLoadResult<V> loadResult = tryLoad(bizKey, bizField);
        if (loadResult.isLoaded()) {
            recorder.sourceLoaded(bizField);
            result.setValueHolder(loadResult.getValueHolder());
        } else if (loadResult.isNoValue()) {
            recorder.sourceLoadNoValue(bizField);
        } else {
            recorder.sourceLoadError(bizField);
        }
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

    private CacheResult<K, F, V> end(DefaultCacheAccessRecorder<K, F> recorder,
                                     CacheResult<K, F, V> result) {
        recorder.end();
        result.setTrace(CacheAccessContext.getTrace());
        return result;
    }
}
