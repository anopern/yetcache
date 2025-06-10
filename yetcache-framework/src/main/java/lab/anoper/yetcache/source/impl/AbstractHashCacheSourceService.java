package lab.anoper.yetcache.source.impl;

import lab.anoper.yetcache.source.IHashCacheSourceService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/4/27
 */
public abstract class AbstractHashCacheSourceService<E> implements IHashCacheSourceService<E> {
    @Override
    public List<E> queryAll(Long tenantId) {
        return null;
    }

    @Override
    public List<E> queryPage(Long tenantId, Integer pageNo, Integer pageSize) {
        return null;
    }

    @Override
    public E querySingle(Long tenantId, String bizKey, String bizHashKey) {
        return null;
    }

    @Override
    public List<E> queryList(Long tenantId, String bizKey) {
        return Collections.emptyList();
    }

    @Override
    public List<E> queryList(Long tenantId, List<String> bizKeys) {
        return Collections.emptyList();
    }

    @Override
    public List<E> queryList(Long tenantId, Map<String, List<String>> bizKeyMap) {
        return Collections.emptyList();
    }
}
