package com.yetcache.core.result;


import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Getter
public class BatchCacheResult<F, T> extends BaseCacheResult<T> {

    public BatchCacheResult(String componentName, Integer code, String message, T value, HitTierInfo hitTierInfo, ErrorInfo errorInfo, Metadata metadata) {
        super(componentName, code, message, value, hitTierInfo, errorInfo, metadata);
    }

    public BatchCacheResult(String componentName, ResultCode rt, T value, HitTierInfo hitTierInfo, ErrorInfo errorInfo, Metadata metadata) {
        super(componentName, rt.code(), rt.message(), value, hitTierInfo, errorInfo, metadata);
    }

    public static <F, T> BatchCacheResult<F, T> hit(String componentName, T value, HitTier hitTier) {
        DefaultHitTierInfo hitTierInfo = new DefaultHitTierInfo(hitTier);
        return new BatchCacheResult<>(componentName, 0, "", value, hitTierInfo, null, null);
    }

//    //
//    public static <V> BatchCacheResult<V> miss(String componentName) {
//        DefaultHitTierInfo hitTierInfo = new DefaultHitTierInfo(HitTier.NONE);
//        return new BatchCacheResult<>(componentName, 0, "", null, hitTierInfo, null, null);
//    }
//
//    //
//    public static <V> BatchCacheResult<V> fail(String componentName, Throwable throwable) {
//        return new BatchCacheResult<>(componentName, -1, null, null, null, null, null);
//    }
//
//    public static <Void> BatchCacheResult<Void> success(String componentName) {
//        return new BatchCacheResult<>(componentName, 0, "", null, null, null, null);
//    }
//
//    public static <T> BatchCacheResult<T> success(T value) {
//        return new BatchCacheResult<>(null, BaseResultCode.SUCCESS.getCode(),
//                BaseResultCode.SUCCESS.getMessage(), value, null, null, null);
//    }

//
//    @Override
//    public HitTier hitTier() {
//        return this.hitTier;
//    }
}
