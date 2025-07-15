package com.yetcache.core.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;

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
public final class FlatHashStorageResult<F, V>
        extends AbstractStorageResult<Map<F, CacheValueHolder<V>>>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    /* --------------------------------------------------------------------
     * Ctor hidden – use factories
     * ------------------------------------------------------------------ */
    private FlatHashStorageResult(CacheOutcome outcome,
                                  Map<F, CacheValueHolder<V>> value,
                                  CacheAccessTrace trace,
                                  HitTier tier,
                                  boolean fromSource) {
        super(outcome, value, trace, tier, fromSource);
    }

    /* --------------------------------------------------------------------
     * Static factories – keep call-site语义清晰
     * ------------------------------------------------------------------ */
    public static <F, V> FlatHashStorageResult<F, V> hit(
            Map<F, CacheValueHolder<V>> value, HitTier tier) {
        return new FlatHashStorageResult<>(CacheOutcome.HIT,
                Collections.unmodifiableMap(Objects.requireNonNull(value)),
                CacheAccessTrace.start().success(), tier, false);
    }

    public static <F, V> FlatHashStorageResult<F, V> miss(HitTier triedTier) {
        return new FlatHashStorageResult<>(CacheOutcome.MISS,
                Collections.emptyMap(),
                CacheAccessTrace.start().success(), triedTier, false);
    }

    public static <F, V> FlatHashStorageResult<F, V> block(String reason) {
        return new FlatHashStorageResult<>(CacheOutcome.BLOCK,
                Collections.emptyMap(),
                CacheAccessTrace.start().block(reason), HitTier.NONE, false);
    }

    public static <F, V> FlatHashStorageResult<F, V> fail(Throwable ex) {
        return new FlatHashStorageResult<>(CacheOutcome.FAIL,
                Collections.emptyMap(),
                CacheAccessTrace.start().fail(ex), HitTier.NONE, false);
    }

    // --- 写操作成功（无实际返回数据）------------------------------------------
    public static <F, V> FlatHashStorageResult<F, V> success() {
        return new FlatHashStorageResult<>(
                CacheOutcome.SUCCESS,
                Collections.emptyMap(),          // 写类操作无需返回具体数据
                CacheAccessTrace.start().success(),
                HitTier.NONE,                    // 写操作不涉及命中层
                false
        );
    }

    /* --------------------------------------------------------------------
     * Shallow copy – complies with CacheAccessResult contract
     * ------------------------------------------------------------------ */
    @Override
    public FlatHashStorageResult<F, V> withTrace(CacheAccessTrace trace) {
        return new FlatHashStorageResult<>(outcome(), value(), trace,
                hitTier(), fromSource());
    }
}
