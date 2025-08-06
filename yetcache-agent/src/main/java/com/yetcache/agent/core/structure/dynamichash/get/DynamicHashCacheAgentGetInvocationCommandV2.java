package com.yetcache.agent.core.structure.dynamichash.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.StructureBehaviorKey;
import com.yetcache.agent.interceptor.v2.CacheInvocationCommandV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicHashCacheAgentGetInvocationCommandV2 implements CacheInvocationCommandV2 {
    private Object bizKey;
    private Object bizField;

    @Override
    public StructureBehaviorKey structureBehaviorKey() {
        return StructureBehaviorKey.of(StructureType.DYNAMIC_HASH, BehaviorType.SINGLE_GET);
    }
}
