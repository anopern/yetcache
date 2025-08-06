package com.yetcache.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Getter
@NoArgsConstructor
public abstract class BaseCacheResultV2<T> implements CacheResult {
    private String componentName;
    private Integer code;
    private String message;
    private T value;
    private HitTierInfo hitTierInfo;
    private ErrorInfo errorInfo;
    private Metadata metadata;

    public BaseCacheResultV2(String componentName, Integer code, String message, T value, HitTierInfo hitTierInfo, ErrorInfo errorInfo, Metadata metadata) {
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
    public Object value() {
        return null;
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
        return false;
    }
}
