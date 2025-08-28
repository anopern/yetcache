package com.yetcache.core.result;

import lombok.AllArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
@AllArgsConstructor
public class ErrorInfo {
    private Throwable throwable;

    public static ErrorInfo of(Throwable throwable) {
        return new ErrorInfo(throwable);
    }
}
