package com.yetcache.agent.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.CacheOutcome;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public final class ConfigCacheAgentBatchAccessResult<F, V>
        extends AbstractCacheAgentResult<Map<F, CacheValueHolder<V>>> {

    /* ---------------- constructors (hidden) ---------------- */
    private ConfigCacheAgentBatchAccessResult(CacheOutcome outcome,
                                              Map<F, CacheValueHolder<V>> value,
                                              HitTier hitTier,
                                              CacheAccessTrace trace,
                                              String componentName) {
        super(outcome, value, hitTier, trace, componentName);
    }

    /* ---------------- static factories --------------------- */

    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> success(String componentName) {
        return new ConfigCacheAgentBatchAccessResult<>(
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
    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> hit(String componentName,
                                                                     Map<F, CacheValueHolder<V>> value,
                                                                     HitTier hitTier) {
        return new ConfigCacheAgentBatchAccessResult<>(
                CacheOutcome.HIT,
                value,
                hitTier,
                CacheAccessTrace.start().success(),
                componentName
        );
    }

    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> flatHashFail(String componentName, Throwable ex) {
        return new ConfigCacheAgentBatchAccessResult<>(
                CacheOutcome.FAIL,
                null,
                null,
                CacheAccessTrace.start().fail(ex),
                componentName
        );
    }

    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> miss(String componentName) {
        return new ConfigCacheAgentBatchAccessResult<>(
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
    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> flatHashBlock(String componentName, String reason) {
        return new ConfigCacheAgentBatchAccessResult<>(
                CacheOutcome.BLOCK,
                null,
                null,
                CacheAccessTrace.start().block(reason),
                componentName
        );
    }
}