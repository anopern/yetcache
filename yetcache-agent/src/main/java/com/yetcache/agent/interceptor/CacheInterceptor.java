package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.StructureBehaviorKey;
import com.yetcache.core.result.CacheResult;


/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInterceptor {

    /**
     * 唯一标识
     */
    String id();

    /**
     * 是否启用
     */
    boolean enabled();

    int getOrder();

    boolean supports(StructureBehaviorKey sbKey);

    CacheResult invoke(CacheInvocationContext context, ChainRunner runner) throws Throwable;
}