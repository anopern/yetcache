package com.yetcache.agent.broadcast.handler;

import com.yetcache.agent.broadcast.command.AbstractCacheBroadcastCommand;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastHandler<T extends AbstractCacheBroadcastCommand> {
    boolean supports(AbstractCacheBroadcastCommand cmd);
    void handle(T cmd);
}
