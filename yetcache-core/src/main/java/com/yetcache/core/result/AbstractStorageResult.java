package com.yetcache.core.result;

import com.yetcache.core.cache.trace.HitTier;

import java.io.Serializable;
import java.util.Objects;


/**
 * Abstract base for all storage‑layer results (L1/L2/SOURCE).
 *
 * @author walter.yan
 * @since 2025/7/13
 */
public abstract class AbstractStorageResult<T>
        implements CacheAccessResult<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private final CacheOutcome outcome;
    private final T value;
    private final CacheAccessTrace trace;
    private final HitTier tier;
    private final boolean fromSource;

    protected AbstractStorageResult(CacheOutcome outcome,
                                    T value,
                                    CacheAccessTrace trace,
                                    HitTier tier,
                                    boolean fromSource) {
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.value = value;
        this.trace = Objects.requireNonNull(trace, "trace");
        this.tier = Objects.requireNonNull(tier, "tier");
        this.fromSource = fromSource;
    }

    /* -------------------------------------------------------------------- */
    @Override
    public CacheOutcome outcome() {
        return outcome;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public CacheAccessTrace trace() {
        return trace;
    }

    public HitTier tierHit() {
        return tier;
    }

    public boolean fromSource() {
        return fromSource;
    }

    /* --------------------------------------------------------------------
     * Subclass must create shallow copy with new trace
     * ------------------------------------------------------------------ */
    @Override
    public abstract AbstractStorageResult<T> withTrace(CacheAccessTrace trace);
}
