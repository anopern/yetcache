package com.yetcache.agent.core;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public enum CacheStructureType {
    DYNAMIC_HASH,
    KV,
    FLAT_HASH;

    public static CacheStructureType fromString(String type) {
        for (CacheStructureType value : CacheStructureType.values()) {
            if (value.name().equalsIgnoreCase(type)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown structureType: " + type);
    }
}
