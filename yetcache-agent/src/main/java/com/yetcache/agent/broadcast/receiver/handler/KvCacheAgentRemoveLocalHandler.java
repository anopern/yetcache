package com.yetcache.agent.broadcast.receiver.handler;

import com.yetcache.agent.broadcast.CacheRemoveCommand;
import com.yetcache.agent.agent.StructureType;
import com.yetcache.agent.agent.CacheAgentPortRegistry;
import com.yetcache.agent.agent.kv.port.KvCacheAgentRemovePort;
import com.yetcache.agent.agent.BehaviorType;
import lombok.AllArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/27
 */
@AllArgsConstructor
public class KvCacheAgentRemoveLocalHandler implements CacheBroadcastHandler {
    private final CacheAgentPortRegistry portRegistry;

    @Override
    public boolean supports(StructureType structureType) {
        return StructureType.KV.equals(structureType);
    }

    @Override
    public void handle(CacheRemoveCommand cmd) {
        KvCacheAgentRemovePort port = (KvCacheAgentRemovePort) portRegistry.get(cmd.getCacheAgentName(),
                BehaviorType.REMOVE);
        port.removeLocal(cmd.getKey());
    }
}
