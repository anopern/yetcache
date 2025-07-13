//package com.yetcache.core.cache.flathash;
//
//import com.yetcache.core.cache.support.CacheValueHolder;
//import com.yetcache.core.config.PenetrationProtectConfig;
//import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
//import com.yetcache.core.support.field.FieldConverter;
//import java.util.Map;
//
///**
// * @author walter.yan
// * @since 2025/7/12
// */
//public class FlatHashPenetrationProtectDecorator<F, V> implements MultiTierFlatHashCache<F, V> {
//    private final String cacheName;
//    private final MultiTierFlatHashCache<F, V> delegate;
//    private final PenetrationProtectConfig config;
//    private final FieldConverter<F> fieldConverter;
//    private final CaffeinePenetrationProtectCache localNullBlockCache;
//
//    public FlatHashPenetrationProtectDecorator(String cacheName,
//                                               MultiTierFlatHashCache<F, V> delegate,
//                                               PenetrationProtectConfig config,
//                                               FieldConverter<F> fieldConverter) {
//        this.cacheName = cacheName;
//        this.delegate = delegate;
//        this.config = config;
//        this.fieldConverter = fieldConverter;
//
//        if (config.getEnabled()) {
//            this.localNullBlockCache = new CaffeinePenetrationProtectCache(config.getPrefix(), cacheName,
//                    config.getTtlSecs(), config.getMaxSize());
//        } else {
//            this.localNullBlockCache = null;
//        }
//    }
//
//    @Override
//    public V get(F field) {
//        FlatHashAccessResult<CacheValueHolder<V>> result = getWithResult(field);
//        return result != null && result.getValue() != null ? result.getValue().getValue() : null;
//    }
//
//    @Override
//    public FlatHashAccessResult<CacheValueHolder<V>> getWithResult(F bizField) {
//        if (!config.getEnabled()) {
//            return delegate.getWithResult(bizField);
//        }
//        String field = fieldConverter.convert(bizField);
//        if (localNullBlockCache.isBlocked(field)) {
//            FlatHashAccessResult<CacheValueHolder<V>> blockedResult = new FlatHashAccessResult<>();
//            blockedResult.setTrace(FlatHashCacheAccessTrace.blocked());
//            return blockedResult;
//        }
//        FlatHashAccessResult<CacheValueHolder<V>> result = delegate.getWithResult(bizField);
//        // 若访问后发现结构未命中，则记录 null 缓存防穿透
//        if (result == null || result.getValue() == null || result.getValue().getValue() == null) {
//            localNullBlockCache.markMiss(field);
//        }
//
//        return result;
//    }
//
//    @Override
//    public Map<F, V> listAll() {
//        return delegate.listAll();
//    }
//
//    @Override
//    public FlatHashAccessResult<Map<F, CacheValueHolder<V>>> listAllWithResult() {
//        return delegate.listAllWithResult();
//    }
//
//    @Override
//    public void putAll(Map<F, V> dataMap) {
//        delegate.putAll(dataMap);
//    }
//}
