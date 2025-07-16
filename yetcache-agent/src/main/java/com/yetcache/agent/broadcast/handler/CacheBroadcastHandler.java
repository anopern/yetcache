package com.yetcache.agent.broadcast.handler;

import com.yetcache.agent.broadcast.CacheBroadcastCommand;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastHandler {
    boolean supports(CacheBroadcastCommand cmd);
    void handle(CacheBroadcastCommand cmd);
}
