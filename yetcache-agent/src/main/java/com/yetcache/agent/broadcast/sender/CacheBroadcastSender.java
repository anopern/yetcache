package com.yetcache.agent.broadcast.sender;

import com.yetcache.agent.broadcast.command.AbstractCacheBroadcastCommand;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastSender {
    void send(AbstractCacheBroadcastCommand command);
}
