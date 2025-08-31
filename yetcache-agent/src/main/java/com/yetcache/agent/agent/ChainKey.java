package com.yetcache.agent.agent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class ChainKey {
    private StructureType structureType;
    private BehaviorType behaviorType;
    private String cacheAgentName;
    private int hash;

    private ChainKey(StructureType structureType, BehaviorType behaviorType, String cacheAgentName) {
        this.structureType = Objects.requireNonNull(structureType);
        this.behaviorType = Objects.requireNonNull(behaviorType);
        this.cacheAgentName = cacheAgentName;
        this.hash = Objects.hash(structureType, behaviorType, cacheAgentName);
    }

    public static ChainKey of(StructureType structureType, BehaviorType behaviorType, String cacheAgentName) {
        return new ChainKey(structureType, behaviorType, cacheAgentName);
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
        if (!(obj instanceof ChainKey)) return false;
        ChainKey that = (ChainKey) obj;
        return this.structureType == that.structureType
                && this.behaviorType == that.behaviorType
                && Objects.equals(this.cacheAgentName, that.cacheAgentName);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return structureType + "::" + behaviorType + "::" + cacheAgentName;
    }
}

