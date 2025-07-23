package com.yetcache.agent.broadcast.handler;

import com.yetcache.agent.broadcast.AbstractCacheBroadcastCommand;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastHandler {
    boolean supports(AbstractCacheBroadcastCommand cmd);
    void handle(AbstractCacheBroadcastCommand cmd);
}
