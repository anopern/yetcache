package com.yetcache.core.support.trace.kv;

import com.yetcache.core.support.trace.dynamichash.SourceLoadStatus;
import com.yetcache.core.support.trace.dynamichash.BaseCacheAccessTrace;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KVCacheAccessTrace<K> extends BaseCacheAccessTrace {
    protected K bizKey;
    protected SourceLoadStatus loadStatus;
}
