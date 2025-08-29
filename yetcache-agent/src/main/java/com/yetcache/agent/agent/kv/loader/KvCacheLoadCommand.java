package com.yetcache.agent.agent.kv.loader;

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
public class KvCacheLoadCommand<K> {
    private K bizKey;
    public static <K> KvCacheLoadCommand<K> of(K bizKey) {
        return new KvCacheLoadCommand<>(bizKey);
    }
}
