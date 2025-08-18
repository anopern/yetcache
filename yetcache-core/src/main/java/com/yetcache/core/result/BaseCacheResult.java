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


    public BaseCacheResult(String componentName, ResultCode resultCode, T value, HitTierInfo hitTierInfo,
                           ErrorInfo errorInfo, Metadata metadata) {
        this.componentName = componentName;
        this.code = resultCode.code();
        this.message = resultCode.message();
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
        return this.hitTierInfo;
    }

    @Override
    public boolean isSuccess() {
        return BaseResultCode.SUCCESS.code().equals(this.code);
    }

    public static <T> BaseCacheResult<T> singleHit(String componentName, T value, HitTier hitTier) {
        DefaultHitTierInfo hitTierInfo = new DefaultHitTierInfo(hitTier);
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS, value, hitTierInfo, null, null);
    }

    public static <T> BaseCacheResult<T> singleHit(String componentName, T value, HitTierInfo hitTierInfo) {
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS, value, hitTierInfo, null, null);
    }

    public static <T> BaseCacheResult<T> batchHit(String componentName, T value, HitTierInfo hitTierInfo) {
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS, value, hitTierInfo, null, null);
    }

    public static <T> BaseCacheResult<T> fail(String componentName, Throwable e) {
        ErrorInfo errorInfo = new ErrorInfo(e);
        return new BaseCacheResult<>(componentName, BaseResultCode.FAIL, null, null, errorInfo, null);
    }

    public static <T> BaseCacheResult<T> success(String componentName) {
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS, null, null, null, null);
    }

    public static <T> BaseCacheResult<T> success(String componentName, T value) {
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS, value, null, null, null);
    }

    public static <T> BaseCacheResult<T> miss(String componentName) {
        DefaultHitTierInfo hitTierInfo = new DefaultHitTierInfo(HitTier.NONE);
        return new BatchCacheResult<>(componentName, 0, "", null, hitTierInfo, null, null);
    }

}
