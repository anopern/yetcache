package com.yetcache.source;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
public interface ISingleHashCacheSourceService<E> {
    List<E> queryAll(@Nullable Long tenantId);
}
