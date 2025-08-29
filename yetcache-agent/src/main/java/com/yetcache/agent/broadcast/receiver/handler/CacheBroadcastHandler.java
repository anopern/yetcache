package com.yetcache.agent.broadcast.receiver.handler;

import com.yetcache.agent.broadcast.CacheRemoveCommand;
import com.yetcache.agent.agent.StructureType;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastHandler {
    boolean supports(StructureType structureType);

    void handle(CacheRemoveCommand cmd);
}
