package com.yetcache.agent.agent.kv.port;

import com.yetcache.agent.agent.CacheAgentPutOptions;
import com.yetcache.agent.agent.StructureType;
import com.yetcache.agent.agent.kv.KvCacheAgent;
import com.yetcache.agent.agent.BehaviorType;
import com.yetcache.agent.agent.StructureBehaviorKey;
import com.yetcache.core.config.CacheLevel;
import com.yetcache.core.result.CacheResult;
import lombok.AllArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/27
 */
@AllArgsConstructor
public class DefaultKvCacheAgentPutPort implements KvCacheAgentPutPort {
    private final KvCacheAgent kvCacheAgent;

    @Override
    public boolean support(StructureBehaviorKey sbKey) {
        return StructureType.KV.equals(sbKey.getStructureType())
                && BehaviorType.PUT.equals(sbKey.getBehaviorType());
    }

    @Override
    public CacheResult put(Object bizKey, Object value) {
        return kvCacheAgent.put(bizKey, value);
    }
}
