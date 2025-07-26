package com.yetcache.agent.broadcast.receiver;

import com.yetcache.agent.broadcast.command.ExecutableCommand;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastReceiver {
    void onReceive(ExecutableCommand cmd);
}
