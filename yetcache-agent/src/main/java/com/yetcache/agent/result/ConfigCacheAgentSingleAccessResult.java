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
public final class ConfigCacheAgentSingleAccessResult<F, V>
        extends AbstractCacheAgentResult<Map<F, CacheValueHolder<V>>> {

    /* ---------------- constructors (hidden) ---------------- */
    private ConfigCacheAgentSingleAccessResult(CacheOutcome outcome,
                                               Map<F, CacheValueHolder<V>> value,
                                               HitTier hitTier,
                                               CacheAccessTrace trace,
                                               String componentName) {
        super(outcome, value, hitTier, trace, componentName);
    }

    /* ---------------- static factories --------------------- */

    public static <F, V> ConfigCacheAgentSingleAccessResult<F, V> success(String componentName) {
        return new ConfigCacheAgentSingleAccessResult<>(
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
    public static <F, V> ConfigCacheAgentSingleAccessResult<F, V> hit(String componentName,
                                                                      Map<F, CacheValueHolder<V>> value,
                                                                      HitTier hitTier) {
        return new ConfigCacheAgentSingleAccessResult<>(
                CacheOutcome.HIT,
                value,
                hitTier,
                CacheAccessTrace.start().success(),
                componentName
        );
    }

    public static <F, V> ConfigCacheAgentSingleAccessResult<F, V> flatHashFail(String componentName, Throwable ex) {
        return new ConfigCacheAgentSingleAccessResult<>(
                CacheOutcome.FAIL,
                null,
                null,
                CacheAccessTrace.start().fail(ex),
                componentName
        );
    }

    public static <F, V> ConfigCacheAgentSingleAccessResult<F, V> miss(String componentName) {
        return new ConfigCacheAgentSingleAccessResult<>(
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
    public static <F, V> ConfigCacheAgentSingleAccessResult<F, V> flatHashBlock(String componentName, String reason) {
        return new ConfigCacheAgentSingleAccessResult<>(
                CacheOutcome.BLOCK,
                null,
                null,
                CacheAccessTrace.start().block(reason),
                componentName
        );
    }
}