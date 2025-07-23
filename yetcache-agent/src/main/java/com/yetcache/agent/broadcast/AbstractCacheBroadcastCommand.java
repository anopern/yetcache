package com.yetcache.agent.broadcast;

import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import lombok.Data;

import java.util.Map;
import java.util.function.Function;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Data
public abstract class AbstractCacheBroadcastCommand {
    protected CacheStructureType structureType;
    protected String agentName;
    protected CacheAgentMethod action;
    protected String key;
    protected Map<String, String> extra;

    public <K> K parseKey(Function<String, K> parser) {
        return parser.apply(this.key);
    }
}