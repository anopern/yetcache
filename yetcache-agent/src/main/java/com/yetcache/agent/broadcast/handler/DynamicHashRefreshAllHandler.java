package com.yetcache.agent.broadcast.handler;

import com.yetcache.agent.broadcast.AbstractCacheBroadcastCommand;
import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheAgent;

/**
 * @author walter.yan
 * @since 2025/7/16
 * 处理 DynamicHash 结构的 REFRESH_ALL 广播命令
 */
public class DynamicHashRefreshAllHandler implements CacheBroadcastHandler {

    private final DynamicHashCacheAgent<?, ?, ?> agent;

    public DynamicHashRefreshAllHandler(DynamicHashCacheAgent<?, ?, ?> agent) {
        this.agent = agent;
    }

    @Override
    public boolean supports(AbstractCacheBroadcastCommand command) {
        return CacheStructureType.DYNAMIC_HASH.equals(command.getStructureType()) &&
                CacheAgentMethod.REFRESH_ALL == command.getAction();
    }

    @Override
    public void handle(AbstractCacheBroadcastCommand cmd) {
//        String key = cmd.getKey();
//        if (key != null) {
//            agent.refreshAll(keyStr -> keyConverter.revert(keyStr));
//        } else {
//            System.err.println("[YetCache] REFRESH_ALL broadcast missing key, command: " + cmd);
//        }
    }
}
