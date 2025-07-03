package com.yetcache.core.support.trace.dynamichash;

import com.alibaba.fastjson2.JSON;
import com.yetcache.core.cache.result.BaseCacheGetResult;
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
public class DynamicHashCacheBatchGetResult<K, F, V> extends BaseCacheGetResult<V> {
    protected DynamicHashCacheBatchAccessTrace<K, F> trace;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
