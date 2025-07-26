package com.yetcache.agent.broadcast.command.descriptor;

import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import lombok.Getter;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Getter
public class CommandDescriptor {
    protected CacheStructureType structureType;
    protected String agentName;
    protected CacheAgentMethod action;
    protected String instanceId;
    protected Map<String, String> extra;

    public CommandDescriptor(CacheStructureType structureType,
                             String agentName,
                             CacheAgentMethod action,
                             String instanceId) {
        this(structureType, agentName, action, instanceId, null);
    }

    public CommandDescriptor(CacheStructureType structureType,
                             String agentName,
                             CacheAgentMethod action,
                             String instanceId,
                             Map<String, String> extra) {
        this.structureType = structureType;
        this.agentName = agentName;
        this.action = action;
        this.instanceId = instanceId;
        this.extra = extra;
    }
}
