package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;
import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
@Getter
public class StructureBehaviorKey {
    private final StructureType structureType;
    private final BehaviorType behaviorType;

    public StructureBehaviorKey(StructureType structureType, BehaviorType behaviorType) {
        this.structureType = structureType;
        this.behaviorType = behaviorType;
    }
}
