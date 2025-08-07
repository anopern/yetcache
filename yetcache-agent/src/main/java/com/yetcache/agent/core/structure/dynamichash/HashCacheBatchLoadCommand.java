package com.yetcache.agent.core.structure.dynamichash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HashCacheBatchLoadCommand {
    private Object bizKey;
    private List<Object> bizFields;
}
