package com.yetcache.agent.broadcast.command.descriptor;

import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
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
}
