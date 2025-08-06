package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.get.DynamicHashCacheGetContext;
import com.yetcache.core.result.BaseSingleResult;
import com.yetcache.core.result.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInterceptor<C extends CacheInvocationContext, T, R extends Result<T>> {

    /**
     * 唯一标识
     */
    String id();

    /**
     * 是否启用
     */
    boolean enabled();

    int getOrder();

    /**
     * 支持的行为类型
     */
    boolean supportBehavior(BehaviorType type);

    /**
     * 支持的结构类型
     */
    boolean supportStructure(StructureType type);

    boolean supportStructureAndBehavior(StructureBehaviorKey structureBehaviorKey);

    R invoke(C context, CacheInvocationChain<C, T, R> chain) throws Throwable;
}