package com.yetcache.agent.agent.kv.loader;

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
public class KvCacheBatchLoadCommand<K> {
    private List<K> bizKeys;
    public static <K> KvCacheBatchLoadCommand<K> of(List<K> bizKeys) {
        return new KvCacheBatchLoadCommand<>(bizKeys);
    }
}
