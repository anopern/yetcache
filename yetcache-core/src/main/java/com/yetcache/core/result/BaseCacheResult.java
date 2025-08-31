package com.yetcache.core.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Getter
@NoArgsConstructor
@ToString
public class BaseCacheResult<T> implements CacheResult {
    private String componentName;
    private Integer code;
    private String message;
    private T value;
    private HitLevelInfo hitLevelInfo;
    private FreshnessInfo freshnessInfo;
    private ErrorInfo errorInfo;

    public BaseCacheResult(String componentName,
                           Integer code,
                           String message) {
        this.componentName = componentName;
        this.code = code;
        this.message = message;
    }

    public BaseCacheResult(String componentName,
                           Integer code,
                           String message,
                           T value,
                           HitLevelInfo hitLevelInfo,
                           FreshnessInfo freshnessInfo,
                           ErrorInfo errorInfo) {
        this.componentName = componentName;
        this.code = code;
        this.message = message;
        this.value = value;
        this.hitLevelInfo = hitLevelInfo;
        this.freshnessInfo = freshnessInfo;
        this.errorInfo = errorInfo;
    }

    public BaseCacheResult(String componentName,
                           ResultCode resultCode,
                           T value, HitLevelInfo hitLevelInfo,
                           FreshnessInfo freshnessInfo) {
        this.componentName = componentName;
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.value = value;
        this.hitLevelInfo = hitLevelInfo;
        this.freshnessInfo = freshnessInfo;
    }

    public BaseCacheResult(String componentName,
                           ResultCode resultCode,
                           T value, HitLevelInfo hitLevelInfo,
                           FreshnessInfo freshnessInfo,
                           ErrorInfo errorInfo) {
        this.componentName = componentName;
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.value = value;
        this.hitLevelInfo = hitLevelInfo;
        this.freshnessInfo = freshnessInfo;
        this.errorInfo = errorInfo;
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
        return this.errorInfo;
    }

    @Override
    public HitLevelInfo hitLevelInfo() {
        return this.hitLevelInfo;
    }

    @Override
    public FreshnessInfo freshnessInfo() {
        return this.freshnessInfo;
    }

    @Override
    public boolean isSuccess() {
        return BaseResultCode.SUCCESS.code().equals(this.code);
    }

    public static <T> BaseCacheResult<T> singleHit(String componentName,
                                                   T value,
                                                   HitLevel hitLevel,
                                                   FreshnessInfo freshnessInfo) {
        DefaultHitLevelInfo hitLevelInfo1 = new DefaultHitLevelInfo(hitLevel);
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS, value, hitLevelInfo1, freshnessInfo);
    }

    public static <T> BaseCacheResult<T> fail(String componentName,
                                              ErrorInfo errorInfo) {
        return new BaseCacheResult<>(componentName, BaseResultCode.FAIL, null, null, null,
                errorInfo);
    }

    public static <T> BaseCacheResult<T> fail(String componentName, Throwable e) {
        ErrorInfo errorInfo = ErrorInfo.of(ErrorDomain.UNKNOWN, ErrorReason.UNKNOWN, e);
        return new BaseCacheResult<>(componentName, BaseResultCode.FAIL, null, null, null,
                errorInfo);
    }

    public static <T> BaseCacheResult<T> success(String componentName) {
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS.code(), BaseResultCode.SUCCESS.getMessage());
    }

    public static <T> BaseCacheResult<T> success(String componentName, T value) {
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS, value, null, null,
                null);
    }

    public static <T> BaseCacheResult<T> miss(String componentName) {
        DefaultHitLevelInfo hitLevelInfo = new DefaultHitLevelInfo(HitLevel.NONE);
        return new BaseCacheResult<>(componentName, BaseResultCode.SUCCESS, null, hitLevelInfo, null,
                null);
    }
}
