package com.yetcache.agent.broadcast.command.descriptor;

import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import lombok.AllArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@AllArgsConstructor
public class CommandDescriptorKey {
    private final CacheStructureType structureType;
    private final CacheAgentMethod action;
}
