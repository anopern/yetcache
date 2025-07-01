package com.yetcache.core.cache.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
@Data
public class BaseCacheResultV2<V> {
    protected CacheValueHolder<V> valueHolder;
}
