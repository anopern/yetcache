package com.yetcache.agent.broadcast.receiver.handler;

import com.yetcache.agent.broadcast.command.TypedPayloadResolver;
import com.yetcache.agent.broadcast.command.ExecutableCommand;
import com.yetcache.agent.broadcast.command.descriptor.CommandDescriptor;
import com.yetcache.agent.broadcast.command.playload.data.DynamicHashData;
import com.yetcache.agent.broadcast.inspector.CommandArrivalInspector;
import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheAgent;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/16
 * 处理 DynamicHash 结构的 REFRESH_ALL 广播命令
 */
@Slf4j
public class DynamicHashPutAllHandler<K, F, V> implements CacheBroadcastHandler {
    protected final CacheAgentRegistryHub cacheAgentRegistryHub;
    protected final CommandArrivalInspector commandArrivalInspector;

    public DynamicHashPutAllHandler(CacheAgentRegistryHub cacheAgentRegistryHub,
                                    CommandArrivalInspector commandArrivalInspector) {
        this.cacheAgentRegistryHub = cacheAgentRegistryHub;
        this.commandArrivalInspector = commandArrivalInspector;
    }

    @Override
    public boolean supports(ExecutableCommand cmd) {
        CommandDescriptor descriptor = cmd.getDescriptor();
        return null != descriptor
                && CacheStructureType.DYNAMIC_HASH.equals(descriptor.getStructureType())
                && CacheAgentMethod.PUT_ALL == descriptor.getAction();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle(ExecutableCommand cmd) {
        if (commandArrivalInspector.isTooLate(cmd)) {

        }
        Optional<CacheAgent> optional = cacheAgentRegistryHub.find(cmd.getDescriptor().getAgentName());
        if (optional.isPresent()) {
            CacheAgent agent = optional.get();
            if (agent instanceof DynamicHashCacheAgent) {
                DynamicHashCacheAgent<K, F, V> dynamicHashCacheAgent = (DynamicHashCacheAgent<K, F, V>) agent;
                DynamicHashData<K, F, V> data = TypedPayloadResolver.resolveByAgent(cmd, dynamicHashCacheAgent);
                dynamicHashCacheAgent.putAll(data.getBizKey(), data.getValueMap());
            } else {
                log.error("缓存代理类 {} 不是 DynamicHashCacheAgent 类型", agent.getClass().getName());
            }
        }
    }
}
