package com.yetcache.agent.core.structure.dynamichash.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.interceptor.BaseInvocationContext;
import com.yetcache.agent.interceptor.BehaviorType;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Getter
public class DynamicHashGetContext<K, F> extends BaseInvocationContext {
    private final K bizKey;
    private final F bizField;

    public DynamicHashGetContext(String componentNane,
                                 String methodName,
                                 StructureType structureType,
                                 BehaviorType behaviorType,
                                 K bizKey,
                                 F bizField) {
        super(componentNane, methodName, structureType, behaviorType);
        this.bizKey = bizKey;
        this.bizField = bizField;
    }
}
