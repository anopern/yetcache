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
    private ErrorDomain domain;
    private ErrorReason reason;
    private Throwable throwable;

    public static ErrorInfo of(ErrorDomain domain, ErrorReason reason, Throwable throwable) {
        return new ErrorInfo(domain, reason, throwable);
    }
}
