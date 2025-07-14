package com.yetcache.core.result;

import com.yetcache.core.cache.trace.HitTier;
import lombok.Getter;

import java.util.Objects;

/**
 * Default unified implementation of CacheAccessResult for storage layer.
 * Immutable, traceable, and behavior-focused.
 *
 * @author walter.yan
 * @since 2025/7/15
 */
@Getter
public final class StorageCacheAccessResult<T> implements CacheAccessResult<T> {
    private CacheOutcome outcome;
    private T value;
    private HitTier tier;
    private CacheAccessTrace trace;
    private Throwable error;

    private StorageCacheAccessResult(CacheOutcome outcome, T value, HitTier tier, Throwable error,
                                     CacheAccessTrace trace) {
        this.outcome = Objects.requireNonNull(outcome);
        this.value = value;
        this.tier = tier;
        this.error = error;
        this.trace = trace;
    }

    private StorageCacheAccessResult(CacheOutcome outcome, T value, HitTier tier, CacheAccessTrace trace) {
        this.outcome = Objects.requireNonNull(outcome);
        this.value = value;
        this.tier = tier;
        this.trace = trace;
    }

    // ========= 静态构造器（工厂方法） ==========

    public static <T> StorageCacheAccessResult<T> hit(T value, HitTier hitTier) {
        return new StorageCacheAccessResult<>(CacheOutcome.SUCCESS, value, hitTier, CacheAccessTrace.start());
    }

    public static <T> StorageCacheAccessResult<T> hitHolderMap(T value, HitTier hitTier) {
        return new StorageCacheAccessResult<>(CacheOutcome.SUCCESS, value, hitTier, CacheAccessTrace.start());
    }

    public static <T> StorageCacheAccessResult<T> success() {
        return new StorageCacheAccessResult<>(CacheOutcome.SUCCESS, null, null, CacheAccessTrace.start());
    }

    public static <T> StorageCacheAccessResult<T> miss() {
        return new StorageCacheAccessResult<>(CacheOutcome.MISS, null, null, CacheAccessTrace.start());
    }

    @Override
    public CacheOutcome outcome() {
        return this.outcome;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public CacheAccessTrace trace() {
        return this.trace;
    }

    @Override
    public StorageCacheAccessResult<T> withTrace(CacheAccessTrace trace) {
        return new StorageCacheAccessResult<>(this.outcome, this.value, null, this.error, trace);
    }
}