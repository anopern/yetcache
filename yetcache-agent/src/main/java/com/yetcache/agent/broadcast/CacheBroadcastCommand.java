package com.yetcache.agent.broadcast;

import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import lombok.Data;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Data
public class CacheBroadcastCommand {
    private CacheStructureType structureType; // DYNAMIC_HASH / KV / FLAT_HASH
    private String agentName;
    private CacheAgentMethod action; // e.g., REFRESH_ALL / REMOVE_FIELD
    private String key;
    private String field;
    private Map<String, String> extra;

    // getters/setters omitted for brevity
}