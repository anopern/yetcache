package com.yetcache.agent.core;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public enum StructureType {
    HASH,
    KV,
    CONFIG;

    public static StructureType fromString(String type) {
        for (StructureType value : StructureType.values()) {
            if (value.name().equalsIgnoreCase(type)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown structureType: " + type);
    }
}
