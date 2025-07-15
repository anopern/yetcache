package com.yetcache.agent.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.CacheOutcome;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public final class FlatHashCacheAgentResult<F, V>
        extends CacheAgentResult<Map<F, CacheValueHolder<V>>> {

    /* ---------------- constructors (hidden) ---------------- */
    private FlatHashCacheAgentResult(CacheOutcome outcome,
                                     Map<F, CacheValueHolder<V>> value,
                                     HitTier hitTier,
                                     CacheAccessTrace trace,
                                     String componentName) {
        super(outcome, value, hitTier, trace, componentName);
    }

    /* ---------------- static factories --------------------- */

    public static <F, V> FlatHashCacheAgentResult<F, V> success(String componentName) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.SUCCESS,
                null,
                null,
                CacheAccessTrace.start().success(),
                componentName
        );
    }

    /**
     * 写类成功（无返回数据）
     */
    public static <F, V> FlatHashCacheAgentResult<F, V> hit(String componentName,
                                                            Map<F, CacheValueHolder<V>> value,
                                                            HitTier hitTier) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.HIT,
                value,
                hitTier,
                CacheAccessTrace.start().success(),
                componentName
        );
    }

    public static <F, V> FlatHashCacheAgentResult<F, V> fail(String componentName, Throwable ex) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.FAIL,
                null,
                null,
                CacheAccessTrace.start().fail(ex),
                componentName
        );
    }

    public static <F, V> FlatHashCacheAgentResult<F, V> miss(String componentName) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.MISS,
                null,
                null,
                CacheAccessTrace.start().success(),
                componentName
        );
    }

    /**
     * 因限流/阻断被拒绝。
     */
    public static <F, V> FlatHashCacheAgentResult<F, V> flatHashBlock(String componentName, String reason) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.BLOCK,
                null,
                null,
                CacheAccessTrace.start().block(reason),
                componentName
        );
    }

    /* ---------------- copy-with-trace ---------------------- */
    @Override
    public FlatHashCacheAgentResult<F, V> withTrace(CacheAccessTrace trace) {
        return new FlatHashCacheAgentResult<>(
                outcome(), value(), hitTier, trace, componentName());
    }
}