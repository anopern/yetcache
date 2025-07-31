package com.yetcache.agent.core.structure.dynamichash.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
import com.yetcache.agent.interceptor.BaseCacheInvocationContext;
import com.yetcache.agent.interceptor.BehaviorType;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public class DynamicHashCacheGetContext<K, F, V> extends BaseCacheInvocationContext {
    private final DynamicHashAgentScope<K, F, V> agentScope;
    private final K bizKey;
    private final F bizField;

    public DynamicHashCacheGetContext(String componentNane,
                                      String methodName,
                                      StructureType structureType,
                                      BehaviorType behaviorType,
                                      DynamicHashAgentScope<K, F, V> agentScope,
                                      K bizKey,
                                      F bizField) {
        super(componentNane, methodName, structureType, behaviorType);
        this.bizKey = bizKey;
        this.bizField = bizField;
        this.agentScope = agentScope;
    }
}
