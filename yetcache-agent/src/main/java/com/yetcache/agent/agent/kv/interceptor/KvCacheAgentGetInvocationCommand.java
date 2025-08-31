package com.yetcache.agent.agent.kv.interceptor;

import com.yetcache.agent.agent.StructureType;
import com.yetcache.agent.agent.BehaviorType;
import com.yetcache.agent.interceptor.CacheInvocationCommand;
import com.yetcache.agent.agent.ChainKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KvCacheAgentGetInvocationCommand implements CacheInvocationCommand {
    private String cacheAgentName;
    private Object bizKey;

    public static KvCacheAgentGetInvocationCommand of(String componentName, Object bizKey) {
        return KvCacheAgentGetInvocationCommand.builder()
                .cacheAgentName(componentName)
                .bizKey(bizKey)
                .build();
    }

    @Override
    public ChainKey chainKey() {
        return ChainKey.of(StructureType.KV, BehaviorType.GET, cacheAgentName);
    }
}
