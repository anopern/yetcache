//package com.yetcache.core.cache.flathash;
//
//import com.yetcache.core.cache.trace.HitTier;
//import lombok.Data;
//
///**
// * @author walter.yan
// * @since 2025/7/9
// */
//@Data
//public class FlatHashCacheAccessTrace {
//    protected HitTier hitTier;
//
//    @Override
//    public String toString() {
//        return "FlatHashAccessTrace{" +
//                "hitTier=" + hitTier +
//                '}';
//    }
//
//    public static FlatHashCacheAccessTrace blocked() {
//        FlatHashCacheAccessTrace trace = new FlatHashCacheAccessTrace();
//        trace.setHitTier(HitTier.BLOCKED);
//        return trace;
//    }
//
//    public boolean isBlocked() {
//        return HitTier.BLOCKED == getHitTier();
//    }
//
//    public boolean isMiss() {
//        return HitTier.MISS == getHitTier();
//    }
//}
