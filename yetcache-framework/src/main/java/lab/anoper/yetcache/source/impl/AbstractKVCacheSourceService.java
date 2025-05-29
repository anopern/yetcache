package lab.anoper.yetcache.source.impl;


import lab.anoper.yetcache.source.IKVCacheSourceService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author walter.yan
 * @since 2025/5/22
 */

public class AbstractKVCacheSourceService<S> implements IKVCacheSourceService<S> {

    @Override
    public List<S> queryAll(Long tenantId) {
        return new ArrayList<>();
    }

    @Override
    public List<S> queryPage(Long tenantId, Integer pageNo, Integer pageSize) {
        return new ArrayList<>();
    }

    @Override
    public S querySingle(Long tenantId, String bizKey) {
        return null;
    }

    @Override
    public List<S> queryList(Long tenantId, List<String> bizKeys) {
        return new ArrayList<>();
    }
}
