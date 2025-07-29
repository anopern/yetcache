package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
public interface InvocationContext {
    String componentNane();

    StructureType structureType();

    BehaviorType behaviorType();
}
