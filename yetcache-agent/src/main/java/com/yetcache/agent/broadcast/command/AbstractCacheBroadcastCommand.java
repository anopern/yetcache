package com.yetcache.agent.broadcast.command;

import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import lombok.Data;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Data
public abstract class AbstractCacheBroadcastCommand {
    protected CacheStructureType structureType;
    protected String agentName;
    protected CacheAgentMethod action;
    protected Map<String, String> extra;
}