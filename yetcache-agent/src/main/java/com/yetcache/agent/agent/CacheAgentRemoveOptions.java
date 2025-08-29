package com.yetcache.agent.agent;

import com.yetcache.core.config.CacheLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/29
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CacheAgentRemoveOptions {
    private CacheLevel cacheLevel;

    public static CacheAgentRemoveOptions of(CacheLevel cacheLevel) {
        return new CacheAgentRemoveOptions(cacheLevel);
    }
}
