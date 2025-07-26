package com.yetcache.agent.broadcast.receiver.handler;

import com.yetcache.agent.broadcast.command.ExecutableCommand;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastHandler {
    boolean supports(ExecutableCommand cmd);
    void handle(ExecutableCommand cmd);
}
