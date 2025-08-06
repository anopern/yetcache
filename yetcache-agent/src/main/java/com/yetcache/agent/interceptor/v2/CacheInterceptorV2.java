package com.yetcache.agent.interceptor.v2;

import com.yetcache.agent.interceptor.StructureBehaviorKey;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInterceptorV2 {

    /**
     * 唯一标识
     */
    String id();

    /**
     * 是否启用
     */
    boolean enabled();

    int getOrder();

    boolean supportStructureAndBehavior(StructureBehaviorKey structureBehaviorKey);

    CacheResult invoke(CacheInvocationContextV2 ctx, CacheInvocationChainV2 chain) throws Throwable;
}