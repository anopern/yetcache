package com.yetcache.core.result;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
@AllArgsConstructor
@ToString
public class ErrorInfo {
    private Throwable throwable;

    public static ErrorInfo of(Throwable throwable) {
        return new ErrorInfo(throwable);
    }
}
