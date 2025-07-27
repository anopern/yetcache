package com.yetcache.agent.broadcast.sender;

import com.yetcache.agent.broadcast.command.ExecutableCommand;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public interface CacheBroadcastPublisher {
    void publish(ExecutableCommand command);
}
