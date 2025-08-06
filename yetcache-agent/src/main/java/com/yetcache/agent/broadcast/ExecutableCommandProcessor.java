package com.yetcache.agent.broadcast;

import com.yetcache.agent.broadcast.command.ExecutableCommand;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
public interface ExecutableCommandProcessor {
    void process(ExecutableCommand command);
}
