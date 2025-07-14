package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.AbstractStorageResult;
import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.CacheOutcome;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Result DTO specialised for Flat-Hash read operations.
 *
 * @author walter.yan
 * @since 2025/7/13
 */
public final class DynamicHashStorageResult<K, F, V>
        extends AbstractStorageResult<Map<K, Map<F, CacheValueHolder<V>>>>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private DynamicHashStorageResult(CacheOutcome outcome,
                                     Map<K, Map<F, CacheValueHolder<V>>> value,
                                     CacheAccessTrace trace,
                                     HitTier tier,
                                     boolean fromSource) {
        super(outcome, value, trace, tier, fromSource);
    }

    // 命中（返回完整结构）
    public static <K, F, V> DynamicHashStorageResult<K, F, V> hit(
            Map<K, Map<F, CacheValueHolder<V>>> value, HitTier tier) {
        return new DynamicHashStorageResult<>(
                CacheOutcome.HIT,
                Collections.unmodifiableMap(Objects.requireNonNull(value)),
                CacheAccessTrace.start().success(),
                tier,
                false
        );
    }

    // 未命中
    public static <K, F, V> DynamicHashStorageResult<K, F, V> miss(HitTier triedTier) {
        return new DynamicHashStorageResult<>(
                CacheOutcome.MISS,
                Collections.emptyMap(),
                CacheAccessTrace.start().success(),
                triedTier,
                false
        );
    }

    public static <K, F, V> DynamicHashStorageResult<K, F, V> block(String reason) {
        return new DynamicHashStorageResult<>(
                CacheOutcome.BLOCK,
                Collections.emptyMap(),
                CacheAccessTrace.start().block(reason),
                HitTier.NONE,
                false
        );
    }

    public static <K, F, V> DynamicHashStorageResult<K, F, V> fail(Throwable ex) {
        return new DynamicHashStorageResult<>(
                CacheOutcome.FAIL,
                Collections.emptyMap(),
                CacheAccessTrace.start().fail(ex),
                HitTier.NONE,
                false
        );
    }

    public static <K, F, V> DynamicHashStorageResult<K, F, V> success() {
        return new DynamicHashStorageResult<>(
                CacheOutcome.SUCCESS,
                Collections.emptyMap(),
                CacheAccessTrace.start().success(),
                HitTier.NONE,
                false
        );
    }

    @Override
    public DynamicHashStorageResult<K, F, V> withTrace(CacheAccessTrace trace) {
        return new DynamicHashStorageResult<>(outcome(), value(), trace, tierHit(), fromSource());
    }
}
