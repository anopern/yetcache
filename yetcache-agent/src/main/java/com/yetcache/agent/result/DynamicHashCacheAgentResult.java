package com.yetcache.agent.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.CacheOutcome;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public class DynamicHashCacheAgentResult<K, F, V> extends CacheAgentResult<Map<F, CacheValueHolder<V>>> {
    DynamicHashCacheAgentResult(CacheOutcome outcome,
                                Map<F, CacheValueHolder<V>> value,
                                HitTier hitTier,
                                CacheAccessTrace trace,
                                String componentName) {
        super(outcome, value, hitTier, trace, componentName);
    }

    public static <K, F, V> DynamicHashCacheAgentResult<K, F, V> success(String componentName,
                                                                         Map<F, CacheValueHolder<V>> value,
                                                                         HitTier hitTier) {
        return new DynamicHashCacheAgentResult<>(CacheOutcome.HIT,
                value,
                hitTier,
                CacheAccessTrace.start(),
                componentName);
    }

    public static <K, F, V> DynamicHashCacheAgentResult<K, F, V> notFound(String componentName) {
        return new DynamicHashCacheAgentResult<>(CacheOutcome.NOT_FUND, null, null,
                CacheAccessTrace.start(), componentName);
    }

    public static <K, F, V> DynamicHashCacheAgentResult<K, F, V> fail(String componentName, Throwable ex) {
        return new DynamicHashCacheAgentResult<>(CacheOutcome.FAIL, null, null,
                CacheAccessTrace.start().fail(ex), componentName);
    }

    public V getFirstValueOrNull() {
        if (!isSuccess() || value() == null || value().isEmpty()) return null;
        return value().values().iterator().next().getValue();
    }

    public List<V> toValueList() {
        if (!isSuccess() || value() == null || value().isEmpty()) return Collections.emptyList();
        return value().values().stream()
                .map(CacheValueHolder::getValue)
                .collect(Collectors.toList());
    }
}
