package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheSpec;
import com.yetcache.core.metrics.HitTier;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/10
 */
@Slf4j
public class BaseMultiTierFlatHashCache<F, V> implements MultiTierFlatHashCache<F, V> {
    protected String cacheName;
    private final MultiTierFlatHashCacheConfig config;
    private CaffeineFlatHashCache<V> localCache;
    private final KeyConverter<Void> keyConverter;
    private final FieldConverter<F> fieldConverter;

    public BaseMultiTierFlatHashCache(String cacheName,
                                      MultiTierFlatHashCacheConfig config,
                                      KeyConverter<Void> keyConverter,
                                      FieldConverter<F> fieldConverter) {
        this.cacheName = cacheName;
        this.config = config;
        this.keyConverter = keyConverter;
        this.fieldConverter = fieldConverter;
        MultiTierFlatHashCacheSpec spec = config.getSpec();
        if (spec.getCacheTier().useLocal()) {
            this.localCache = new CaffeineFlatHashCache<>(config.getLocal());
        }
    }

    @Override
    public V get(F bizField) {
        FlatHashAccessResult<CacheValueHolder<V>> result = getWithResult(bizField);
        if (result == null || result.getValue() == null) {
            return null;
        }
        return result.getValue().getValue();
    }


    @Override
    public FlatHashAccessResult<CacheValueHolder<V>> getWithResult(F bizField) {
        String key = keyConverter.convert(null);
        String field = fieldConverter.convert(bizField);

        FlatHashAccessResult<CacheValueHolder<V>> result = new FlatHashAccessResult<>();
        FlatHashCacheAccessTrace trace = new FlatHashCacheAccessTrace();
        result.setTrace(trace);

        // === 本地缓存读取 ===
        if (localCache != null) {
            CacheValueHolder<V> holder = localCache.getIfPresent(key, field);
            if (holder != null && holder.isNotLogicExpired()) {
                result.setValue(holder);
                trace.setHitTier(HitTier.LOCAL);
                return result;
            }
        }

        throw new IllegalStateException("no cache found");
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
        FlatHashAccessResult<Map<F, CacheValueHolder<V>>> result = new FlatHashAccessResult<>();
        FlatHashCacheAccessTrace trace = new FlatHashCacheAccessTrace();
        result.setTrace(trace);

        String key = keyConverter.convert(null);
        Map<String, CacheValueHolder<V>> rawMap = localCache.listAll(key);
        Map<F, CacheValueHolder<V>> typedMap = new HashMap<>();
        for (Map.Entry<String, CacheValueHolder<V>> entry : rawMap.entrySet()) {
            try {
                F field = fieldConverter.reverse(entry.getKey());
                typedMap.put(field, entry.getValue());
            } catch (Exception e) {
                log.warn("反序列字段失败：fieldKey={}, err={}", entry.getKey(), e.getMessage(), e);
                // 可选择忽略/中断/记录 trace
            }
        }
        result.setValue(typedMap);
        return result;
    }

    @Override
    public void putAll(Map<F, V> dataMap) {

        if (dataMap == null || dataMap.isEmpty()) {
            return;
        }

        if (localCache == null) {
            throw new IllegalStateException("putAll failed: localCache not configured.");
        }

        String key = keyConverter.convert(null);
        Map<String, CacheValueHolder<V>> cacheMap = new HashMap<>();

        for (Map.Entry<F, V> entry : dataMap.entrySet()) {
            F field = entry.getKey();
            V value = entry.getValue();

            if (field == null || value == null) {
                log.debug("skip null field or value: field={}, value={}", field, value);
                continue;
            }

            String fieldKey = fieldConverter.convert(field);
            CacheValueHolder<V> holder = CacheValueHolder.wrap(value, config.getLocal().getTtlSecs());
            cacheMap.put(fieldKey, holder);
        }

        localCache.putAll(key, cacheMap);
    }

//    @Override
//    public boolean refreshAll() {
//        FlatHashAccessResult<Map<F, CacheValueHolder<V>>> result = refreshAllWithResult();
//        if (null != result.getTrace()) {
//            log.debug("trace=" + result.getTrace());
//        }
//        return null != result.getValue();
//    }
//
//    @Override
//    public FlatHashAccessResult<Map<F, CacheValueHolder<V>>> refreshAllWithResult() {
//        FlatHashAccessResult<Map<F, CacheValueHolder<V>>> result = new FlatHashAccessResult<>();
//        FlatHashAccessTrace trace = new FlatHashAccessTrace();
//        result.setTrace(trace);
//
//        // 1. 加载数据
//        Map<F, V> typeMap = cacheLoader.loadAll();
//        if (typeMap == null || typeMap.isEmpty()) {
//            result.setValue(new HashMap<>());
//            return result;
//        }
//
//        // 2. 包装为 CacheValueHolder 并写入本地缓存
//        Map<F, CacheValueHolder<V>> holderMap = new HashMap<>();
//        Map<String, CacheValueHolder<V>> cacheMap = new HashMap<>();
//        String key = keyConverter.convert(null);
//
//        for (Map.Entry<F, V> entry : typeMap.entrySet()) {
//            F field = entry.getKey();
//            V value = entry.getValue();
//
//            CacheValueHolder<V> holder = CacheValueHolder.wrap(value, Integer.MAX_VALUE);  // 使用你平台封装的静态构造器
//            holderMap.put(field, holder);
//
//            String fieldKey = fieldConverter.convert(field);         // F → String
//            cacheMap.put(fieldKey, holder);
//        }
//
//        // 3. 存入本地缓存（整个 map）
//        localCache.putAll(key, cacheMap);
//
//        // 4. 返回封装结果
//        result.setValue(holderMap);
//        return result;
//    }
}
