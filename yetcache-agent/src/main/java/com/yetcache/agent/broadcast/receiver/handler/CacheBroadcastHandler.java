package com.yetcache.agent.broadcast.receiver.handler;


import com.yetcache.agent.broadcast.command.CacheUpdateCommand;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastHandler {
    boolean supports(CacheUpdateCommand cmd);

    void handle(CacheUpdateCommand cmd);
}
