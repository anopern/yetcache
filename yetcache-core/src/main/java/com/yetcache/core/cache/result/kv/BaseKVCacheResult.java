package com.yetcache.core.cache.result.kv;

import com.yetcache.core.cache.result.BaseCacheResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseKVCacheResult<K> extends BaseCacheResult<BaseKVCacheResult<K>> {
    /**
     * 业务原始 key（未转换前的业务层 key），用于定位调用源头，便于排查。
     */
    protected K bizKey;
}
