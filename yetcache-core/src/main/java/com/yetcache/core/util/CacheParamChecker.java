package com.yetcache.core.util;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class CacheParamChecker {
    public static void failIfNull(Object bizKey, String cacheName) {
        if (bizKey == null) {
            throw new IllegalArgumentException(String.format(
                    "缓存调用失败：业务方传入非法参数 [key=null]，缓存组件拒绝处理。" +
                            "请检查调用逻辑。缓存名：%s",
                    cacheName
            ));
        }
    }

}
