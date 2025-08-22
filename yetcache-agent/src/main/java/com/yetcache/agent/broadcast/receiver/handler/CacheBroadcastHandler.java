package com.yetcache.agent.broadcast.receiver.handler;


import com.yetcache.agent.broadcast.command.CacheCommand;
import com.yetcache.agent.interceptor.StructureBehaviorKey;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastHandler {
    boolean supports(StructureBehaviorKey sbKey);

    void handle(CacheCommand cmd);
}
