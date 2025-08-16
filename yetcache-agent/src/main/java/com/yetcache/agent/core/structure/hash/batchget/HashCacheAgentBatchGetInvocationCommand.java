package com.yetcache.agent.core.structure.hash.batchget;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.CacheInvocationCommand;
import com.yetcache.agent.interceptor.StructureBehaviorKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashCacheAgentBatchGetInvocationCommand implements CacheInvocationCommand {
    private Object bizKey;
    private List<Object> bizFields;

    @Override
    public StructureBehaviorKey structureBehaviorKey() {
        return StructureBehaviorKey.of(StructureType.DYNAMIC_HASH, BehaviorType.BATCH_GET);
    }
}
