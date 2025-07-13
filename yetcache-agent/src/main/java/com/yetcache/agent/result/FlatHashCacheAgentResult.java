package com.yetcache.agent.result;

import com.yetcache.core.cache.support.CacheValueHolder;
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
                                     CacheAccessTrace trace,
                                     String agentName,
                                     boolean fromCache) {
        super(outcome, value, trace, agentName, fromCache);
    }

    /* ---------------- static factories --------------------- */

    /**
     * 写 / 读成功，value 不为空
     */
    public static <F, V> FlatHashCacheAgentResult<F, V> success(String agentName,
                                                                Map<F, CacheValueHolder<V>> value,
                                                                boolean fromCache) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.SUCCESS,
                Collections.unmodifiableMap(Objects.requireNonNull(value)),
                CacheAccessTrace.start().success(),
                agentName,
                fromCache
        );
    }

    /**
     * 写类成功（无返回数据）
     */
    public static <F, V> FlatHashCacheAgentResult<F, V> success(String agentName) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.SUCCESS,
                Collections.emptyMap(),
                CacheAccessTrace.start().success(),
                agentName,
                true
        );
    }

    public static <F, V> FlatHashCacheAgentResult<F, V> flatHashFail(String agentName, Throwable ex) {               // 签名与父类不同 ↓↓↓
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.FAIL,
                Collections.emptyMap(),
                CacheAccessTrace.start().fail(ex),
                agentName,
                true
        );
    }

    public static <F, V> FlatHashCacheAgentResult<F, V> flatHashMiss(String agentName) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.MISS,
                null,
                CacheAccessTrace.start().success(),
                agentName,
                true
        );
    }

    /** 因限流/阻断被拒绝。 */
    public static  <F, V> FlatHashCacheAgentResult<F, V> flatHashBlock(String agentName, String reason) {
        return new FlatHashCacheAgentResult<>(
                CacheOutcome.BLOCK,
                null,
                CacheAccessTrace.start().block(reason),
                agentName,
                true
        );
    }

    /* ---------------- copy-with-trace ---------------------- */
    @Override
    public FlatHashCacheAgentResult<F, V> withTrace(CacheAccessTrace trace) {
        return new FlatHashCacheAgentResult<>(
                outcome(), value(), trace, agentName(), fromCache());
    }
}