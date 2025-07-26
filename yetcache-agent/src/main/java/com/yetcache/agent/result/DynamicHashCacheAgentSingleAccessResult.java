package com.yetcache.agent.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.CacheOutcome;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public final class DynamicHashCacheAgentSingleAccessResult<V> extends BaseResult<CacheValueHolder<V>> {
    private HitTier hitTier;

    DynamicHashCacheAgentSingleAccessResult(String cacheName,
                                            CacheOutcome outcome) {
        this(cacheName, outcome, null, null);
    }

    DynamicHashCacheAgentSingleAccessResult(String cacheName,
                                            Throwable error) {
        this(cacheName, CacheOutcome.FAIL, null, null, error);
    }

    DynamicHashCacheAgentSingleAccessResult(String cacheName,
                                            CacheOutcome outcome,
                                            CacheValueHolder<V> valueHolder,
                                            HitTier hitTier) {
        this(cacheName, outcome, valueHolder, hitTier, null);
    }

    DynamicHashCacheAgentSingleAccessResult(String cacheName,
                                            CacheOutcome outcome,
                                            CacheValueHolder<V> valueHolder,
                                            HitTier hitTier,
                                            Throwable error) {
        super(cacheName, outcome, valueHolder);
        this.hitTier = hitTier;
        this.error = error;
    }

    public static <V> DynamicHashCacheAgentSingleAccessResult<V> hit(String cacheName,
                                                                     CacheValueHolder<V> valueHolder,
                                                                     HitTier hitTier) {
        return new DynamicHashCacheAgentSingleAccessResult<>(cacheName, CacheOutcome.HIT, valueHolder, hitTier);
    }

    public static <V> DynamicHashCacheAgentSingleAccessResult<V> notFound(String cacheName) {
        return new DynamicHashCacheAgentSingleAccessResult<>(cacheName, CacheOutcome.NOT_FOUND);
    }

    public static <V> DynamicHashCacheAgentSingleAccessResult<V> fail(String cacheName, Throwable error) {
        return new DynamicHashCacheAgentSingleAccessResult<>(cacheName, error);
    }

//
//    public static <K, F, V> DynamicHashCacheAgentSingleAccessResult<K, F, V> success(String componentName,
//                                                                                     Map<F, CacheValueHolder<V>> value,
//                                                                                     HitTier hitTier) {
//        return new DynamicHashCacheAgentSingleAccessResult<>(CacheOutcome.HIT,
//                value,
//                hitTier,
//                CacheAccessTrace.start(),
//                componentName);
//    }
//
//    public static <K, F, V> DynamicHashCacheAgentSingleAccessResult<K, F, V> success(String componentName) {
//        return new DynamicHashCacheAgentSingleAccessResult<>(CacheOutcome.SUCCESS,
//                null,
//                null,
//                CacheAccessTrace.start(),
//                componentName);
//    }
//
//    public static <K, F, V> DynamicHashCacheAgentSingleAccessResult<K, F, V> dynamicHashNotFound(String componentName) {
//        return new DynamicHashCacheAgentSingleAccessResult<>(CacheOutcome.NOT_FOUND, null, null,
//                CacheAccessTrace.start(), componentName);
//    }
//
//    public static <K, F, V> DynamicHashCacheAgentSingleAccessResult<K, F, V> dynamicHashFail(String componentName, Throwable ex) {
//        return new DynamicHashCacheAgentSingleAccessResult<>(CacheOutcome.FAIL, null, null,
//                CacheAccessTrace.start().fail(ex), componentName);
//    }
//
//    public V getFirstValueOrNull() {
//        if (!isSuccess() || value() == null || value().isEmpty()) return null;
//        return value().values().iterator().next().getValue();
//    }
//
//    public List<V> toValueList() {
//        if (!isSuccess() || value() == null || value().isEmpty()) return Collections.emptyList();
//        return value().values().stream()
//                .map(CacheValueHolder::getValue)
//                .collect(Collectors.toList());
//    }
//
//    public static <K, F, V> DynamicHashCacheAgentSingleAccessResult<K, F, V> badParam(String componentName) {
//        return new DynamicHashCacheAgentSingleAccessResult<>(CacheOutcome.BAD_PARAM, null, null,
//                CacheAccessTrace.start(), componentName);
//    }
}
