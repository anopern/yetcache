package com.yetcache.agent.core.structure.hash;

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
public class HashCacheBatchLoadCommand<K, F> {
    private K bizKey;
    private List<F> bizFields;

    public static <K, F> HashCacheBatchLoadCommand<K, F> of(K bizKey, List<F> bizFields) {
        return new HashCacheBatchLoadCommand<>(bizKey, bizFields);
    }
}
