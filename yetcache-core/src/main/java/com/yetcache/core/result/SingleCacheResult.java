package com.yetcache.core.result;


import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Getter
public class SingleCacheResult<T> extends BaseCacheResult<T> {

    public SingleCacheResult(String componentName, Integer code, String message, T value, HitTierInfo hitTierInfo, ErrorInfo errorInfo, Metadata metadata) {
        super(componentName, code, message, value, hitTierInfo, errorInfo, metadata);
    }

    public static <T> SingleCacheResult<T> hit(String componentName, T value, HitTier hitTier) {
        DefaultHitTierInfo hitTierInfo = new DefaultHitTierInfo(hitTier);
        return new SingleCacheResult<>(componentName, 0, "", value, hitTierInfo, null, null);
    }

    //
    public static <V> SingleCacheResult<V> miss(String componentName) {
        DefaultHitTierInfo hitTierInfo = new DefaultHitTierInfo(HitTier.NONE);
        return new SingleCacheResult<>(componentName, 0, "", null, hitTierInfo, null, null);
    }

    //
    public static <V> SingleCacheResult<V> fail(String componentName, Throwable throwable) {
        return new SingleCacheResult<>(componentName, -1, null, null, null, null, null);
    }

    public static <Void> SingleCacheResult<Void> success(String componentName) {
        return new SingleCacheResult<>(componentName, 0, "", null, null, null, null);
    }

    public static <T> SingleCacheResult<T> success(T value) {
        return new SingleCacheResult<>(null, BaseResultCode.SUCCESS.getCode(),
                BaseResultCode.SUCCESS.getMessage(), value, null, null, null);
    }

//
//    @Override
//    public HitTier hitTier() {
//        return this.hitTier;
//    }
}
