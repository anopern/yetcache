package com.yetcache.agent.core.structure.dynamichash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HashCacheAgentGetCommand {
    private Object bizKey;
    private Object bizField;
}
