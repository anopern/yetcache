package com.yetcache.agent.broadcast.command.playload.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicHashData<K, F, V> {
    private K bizKey;
    private Map<F, V> valueMap;

    private Long version;
}
