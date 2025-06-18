package com.yetcache.source.impl;

import com.yetcache.source.ISingleHashCacheSourceService;

import java.util.Collections;
import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
public class AbstractSingleHashCacheSourceService<E> implements ISingleHashCacheSourceService<E> {
    @Override
    public List<E> queryAll(Long tenantId) {
        return Collections.emptyList();
    }
}
