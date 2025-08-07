package com.yetcache.core.result;

import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/8/1
 */
public enum BaseResultCode implements ResultCode {
    SUCCESS(0, "操作成功"),
    FAIL(-1, "操作成功"),

    ;

    @Getter
    private final Integer code;

    @Getter
    private final String message;

    BaseResultCode(Integer code, String message) {
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
