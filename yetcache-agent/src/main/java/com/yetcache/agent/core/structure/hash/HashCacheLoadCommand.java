package com.yetcache.agent.core.structure.hash;

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
public class HashCacheLoadCommand<K, F> {
    private K bizKey;
    private F bizField;

    public static <K, F> HashCacheLoadCommand<K, F> of(K bizKey, F bizField) {
        return new HashCacheLoadCommand<>(bizKey, bizField);
    }
}
