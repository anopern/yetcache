package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;
import lombok.Getter;

import java.util.Objects;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
@Getter
public final class StructureBehaviorKey {
    private final StructureType structureType;
    private final BehaviorType behaviorType;
    private final int hash; // 缓存 hashCode

    private StructureBehaviorKey(StructureType structureType, BehaviorType behaviorType) {
        this.structureType = Objects.requireNonNull(structureType);
        this.behaviorType = Objects.requireNonNull(behaviorType);
        this.hash = Objects.hash(structureType, behaviorType);
    }

    public static StructureBehaviorKey of(StructureType structureType, BehaviorType behaviorType) {
        return new StructureBehaviorKey(structureType, behaviorType);
    }

    public StructureType getStructureType() {
        return structureType;
    }

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StructureBehaviorKey)) return false;
        StructureBehaviorKey that = (StructureBehaviorKey) obj;
        return this.structureType == that.structureType && this.behaviorType == that.behaviorType;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return structureType + "::" + behaviorType;
    }
}

