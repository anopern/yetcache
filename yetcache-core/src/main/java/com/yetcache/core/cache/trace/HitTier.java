//package com.yetcache.core.cache.trace;
//
///**
// * @author walter.yan
// * @since 2025/7/3
// */
//public enum HitTier {
//    LOCAL,
//    REMOTE,
//    SOURCE,
//    NONE,
//    MIXED,
//    ;
//
//    public HitTier merge(HitTier other) {
//        if (this == NONE) {
//            return other;
//        }
//        if (other == NONE) {
//            return this;
//        }
//        if (this == other) {
//            return this;
//        }
//        return MIXED;
//    }
//}
