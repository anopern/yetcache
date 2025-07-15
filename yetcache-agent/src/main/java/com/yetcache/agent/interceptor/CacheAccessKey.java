package com.yetcache.agent.interceptor;

import lombok.Getter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Getter
public class CacheAccessKey {
    private final Object bizKey;
    private final Object bizField;

    public CacheAccessKey(@NotNull Object bizKey, @Nullable Object bizField) {
        this.bizKey = bizKey;
        this.bizField = bizField;
    }

    @Override
    public String toString() {
        if (null != bizField) {
            return bizKey + "_" + bizField;
        }
        return bizKey.toString();
    }
}
