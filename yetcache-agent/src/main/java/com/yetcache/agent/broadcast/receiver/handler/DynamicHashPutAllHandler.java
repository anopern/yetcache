package com.yetcache.agent.broadcast.receiver.handler;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.agent.broadcast.command.CacheUpdateCommand;
import com.yetcache.agent.broadcast.command.CommandDescriptor;
import com.yetcache.agent.broadcast.command.Playload;
import com.yetcache.agent.core.PutAllOptions;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheAgent;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.StructureBehaviorKey;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/16
 * 处理 DynamicHash 结构的 REFRESH_ALL 广播命令
 */
@Slf4j
public class DynamicHashPutAllHandler implements CacheBroadcastHandler {
    protected final CacheAgentRegistryHub cacheAgentRegistryHub;

    public DynamicHashPutAllHandler(CacheAgentRegistryHub cacheAgentRegistryHub) {
        this.cacheAgentRegistryHub = cacheAgentRegistryHub;
    }

    @Override
    public boolean supports(CacheUpdateCommand cmd) {
        CommandDescriptor descriptor = cmd.getDescriptor();
        if (null == descriptor) {
            return false;
        }
        StructureBehaviorKey sbKey = descriptor.getStructureBehaviorKey();
        if (null == sbKey) {
            return false;
        }
        return StructureType.DYNAMIC_HASH.equals(sbKey.getStructureType())
                && BehaviorType.PUT_ALL.equals(sbKey.getBehaviorType());
    }

    @Override
    public void handle(CacheUpdateCommand cmd) {
        Playload playload = cmd.getPayload();
        if (null == playload) {
            log.error("缓存代理类 {} 缓存更新命令 {} 缺少 payload", cmd.getDescriptor().getComponentName(), cmd);
            return;
        }
        Object bizKey = playload.getBizKey();
        Map<Object, Object> valueMap = playload.getBizFieldValueMap();
        if (null == bizKey || CollUtil.isEmpty(valueMap)) {
            log.error("缓存代理类 {} 缓存更新命令 {} payload 缺少 bizKey 或 valueMap", cmd.getDescriptor().getComponentName(), cmd);
            return;
        }
        Optional<CacheAgent> optional = cacheAgentRegistryHub.find(cmd.getDescriptor().getComponentName());
        if (optional.isEmpty()) {
            log.error("缓存代理类 {} 不存在", cmd.getDescriptor().getComponentName());
            return;
        }
        CacheAgent agent = optional.get();
        if (agent instanceof DynamicHashCacheAgent) {
            DynamicHashCacheAgent dhAgent = (DynamicHashCacheAgent) agent;
            dhAgent.putAll(bizKey, valueMap, PutAllOptions.builder().broadcast(false).build());
        } else {
            log.error("缓存代理类 {} 不是 DynamicHashCacheAgent 类型", agent.getClass().getName());
        }
    }
}
