package com.yetcache.core.cache.result.dynamichash;

import com.alibaba.fastjson2.JSON;
import com.yetcache.core.cache.result.BaseCacheResultV2;
import com.yetcache.core.support.trace.dynamichash.DynamicHashCacheAccessTrace;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DynamicHashCacheResult<K, F, V> extends BaseCacheResultV2<V> {
    protected DynamicHashCacheAccessTrace<K, F> trace;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
