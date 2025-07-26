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
public final class StorageCacheAccessResultBak<T> implements Result<T> {
    private final CacheOutcome outcome;
    private final T value;
    private final HitTier tier;
    private final CacheAccessTrace trace;

    private Throwable error;

    private StorageCacheAccessResultBak(CacheOutcome outcome, T value, HitTier tier, Throwable error,
                                        CacheAccessTrace trace) {
        this.outcome = Objects.requireNonNull(outcome);
        this.value = value;
        this.tier = tier;
        this.error = error;
        this.trace = trace;
    }

    private StorageCacheAccessResultBak(CacheOutcome outcome, T value, HitTier tier, CacheAccessTrace trace) {
        this.outcome = Objects.requireNonNull(outcome);
        this.value = value;
        this.tier = tier;
        this.trace = trace;
    }

    // ========= 静态构造器（工厂方法） ==========

    public static <T> StorageCacheAccessResultBak<T> hit(T value, HitTier hitTier) {
        return new StorageCacheAccessResultBak<>(CacheOutcome.SUCCESS, value, hitTier, CacheAccessTrace.start());
    }

    public static <T> StorageCacheAccessResultBak<T> hitHolderMap(T value, HitTier hitTier) {
        return new StorageCacheAccessResultBak<>(CacheOutcome.HIT, value, hitTier, CacheAccessTrace.start());
    }

    public static <T> StorageCacheAccessResultBak<T> success() {
        return new StorageCacheAccessResultBak<>(CacheOutcome.SUCCESS, null, null, CacheAccessTrace.start());
    }

    public static <T> StorageCacheAccessResultBak<T> miss() {
        return new StorageCacheAccessResultBak<>(CacheOutcome.MISS, null, null, CacheAccessTrace.start());
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
    public StorageCacheAccessResultBak<T> withTrace(CacheAccessTrace trace) {
        return new StorageCacheAccessResultBak<>(this.outcome, this.value, null, this.error, trace);
    }
}