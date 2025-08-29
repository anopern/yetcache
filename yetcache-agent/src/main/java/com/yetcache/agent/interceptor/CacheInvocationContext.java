package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.AgentScope;
import com.yetcache.core.result.CacheResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class CacheInvocationContext {
    private CacheInvocationCommand command;
    private CacheResult result;
    private AgentScope agentScope;
    private boolean interrupted;
    private String interruptReason;

    public CacheInvocationContext(CacheInvocationCommand command, AgentScope agentScope) {
        this.command = command;
        this.agentScope = agentScope;
    }

    public void interrupt(String reason) {
        interrupted = true;
        interruptReason = reason;
    }
}
