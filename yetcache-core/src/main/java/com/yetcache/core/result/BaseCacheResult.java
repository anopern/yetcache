package com.yetcache.core.result;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Getter
@NoArgsConstructor
public class BaseCacheResult<T> implements CacheResult {
    private String componentName;
    private Integer code;
    private String message;
    private T value;
    private HitTierInfo hitTierInfo;
    private ErrorInfo errorInfo;
    private Metadata metadata;

    public BaseCacheResult(String componentName, Integer code, String message, T value, HitTierInfo hitTierInfo, ErrorInfo errorInfo, Metadata metadata) {
        this.componentName = componentName;
        this.code = code;
        this.message = message;
        this.value = value;
        this.hitTierInfo = hitTierInfo;
        this.errorInfo = errorInfo;
        this.metadata = metadata;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return null;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public ErrorInfo errorInfo() {
        return null;
    }

    @Override
    public Metadata metadata() {
        return null;
    }

    @Override
    public HitTierInfo hitTierInfo() {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return code == 0;
    }

    public static <T> BaseCacheResult<T> fail(String componentName) {
        return new BaseCacheResult<>(componentName, -1, "操作失败", null, null, null, null);
    }
}
