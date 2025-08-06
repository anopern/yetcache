package com.yetcache.core.result;


import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Getter
public class SingleCacheResultV2<T> extends BaseCacheResultV2<T> {

    public SingleCacheResultV2(String componentName, Integer code, String message, T value, HitTierInfo hitTierInfo, ErrorInfo errorInfo, Metadata metadata) {
        super(componentName, code, message, value, hitTierInfo, errorInfo, metadata);
    }


    //    protected HitTier hitTier;
//
//    public SingleCacheResultV2(String componentName, CacheOutcome outcome, CacheValueHolder<V> value, HitTier hitTier, Throwable error) {
//        super(componentName, outcome, value, error);
//        this.hitTier = hitTier;
//    }
//
    public static <T> SingleCacheResultV2<T> hit(String componentName, T value, HitTier hitTier) {
        DefaultHitTierInfo hitTierInfo = new DefaultHitTierInfo(hitTier);
        return new SingleCacheResultV2<>(componentName, 0, "", value, hitTierInfo, null, null);
    }

    //
    public static <V> SingleCacheResultV2<V> miss(String componentName) {
        DefaultHitTierInfo hitTierInfo = new DefaultHitTierInfo(HitTier.NONE);
        return new SingleCacheResultV2<>(componentName, 0, "", null, hitTierInfo, null, null);
    }
//
    public static <V> SingleCacheResultV2<V> fail(String componentName, Throwable throwable) {
        return new SingleCacheResultV2<>(componentName, -1, null, null, null, null, null);
    }
//
//    public static <Void> SingleCacheResultV2<Void> success(String componentName) {
//        return new SingleCacheResultV2<>(componentName, CacheOutcome.SUCCESS, null, null, null);
//    }
//
//    @Override
//    public HitTier hitTier() {
//        return this.hitTier;
//    }
}
