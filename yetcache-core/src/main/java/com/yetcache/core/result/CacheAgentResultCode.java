package com.yetcache.core.result;

import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/8/1
 */
public enum CacheAgentResultCode implements ResultCode {
    SUCCESS(2000, "操作成功");

    @Getter
    private final Integer code;

    @Getter
    private final String message;

    CacheAgentResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer code() {
        return getCode();
    }

    @Override
    public String message() {
        return getMessage();
    }
}
