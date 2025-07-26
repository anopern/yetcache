package com.yetcache.agent.broadcast.receiver.handler;

import com.yetcache.agent.broadcast.command.ExecutableCommand;
import lombok.extern.slf4j.Slf4j;

/**
 * @author walter.yan
 * @since 2025/7/16
 * 处理 DynamicHash 结构的 REFRESH_ALL 广播命令
 */
@Slf4j
public class DynamicHashBatchRefreshHandler implements CacheBroadcastHandler {
    @Override
    public boolean supports(ExecutableCommand cmd) {
        return false;
    }

    @Override
    public void handle(ExecutableCommand cmd) {

    }

//    private final DynamicHashCacheAgent<K, F, V> agent;
//
//    public DynamicHashBatchRefreshHandler(DynamicHashCacheAgent<K, F, V> agent) {
//        this.agent = agent;
//    }
//
//    @Override
//    public boolean supports(AbstractCacheBroadcastCommand command) {
//        return CacheStructureType.DYNAMIC_HASH.equals(command.getStructureType()) &&
//                CacheAgentMethod.BATCH_REFRESH == command.getAction();
//    }
//
//    @Override
//    public void handle(DynamicHashCacheBroadcastCommand<K, F, V> cmd) {
//        if (cmd.getData() == null || cmd.getData().isEmpty()) {
//            log.warn("Received empty data for DynamicHashCacheBroadcastCommand: agent={}, action={}",
//                    cmd.getAgentName(), cmd.getAction());
//            return;
//        }
//
//        try {
//            agent.putAll(cmd.getData());
//            log.debug("Handled DynamicHash cmd: agent={}", cmd.getAgentName());
//        } catch (Exception e) {
//            log.error("Failed to handle DynamicHash BATCH_REFRESH: agent={}", cmd.getAgentName());
//        }
//    }

}
