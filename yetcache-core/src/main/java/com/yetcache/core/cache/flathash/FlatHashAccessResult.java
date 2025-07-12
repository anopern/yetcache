package com.yetcache.core.cache.flathash;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/9
 */
@Data
public class FlatHashAccessResult<T> {
    private boolean success = true;
    private String message;
    private Throwable exception;

    private T value;
    private FlatHashAccessTrace trace;

    public FlatHashAccessResult() {
        this.trace = new FlatHashAccessTrace();
    }

    public static FlatHashAccessResult<Void> success() {
        return new FlatHashAccessResult<>();
    }
}
