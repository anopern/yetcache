package lab.anoper.yetcache.source.impl;

import lab.anoper.yetcache.source.ISingleHashCacheSourceService;
import org.springframework.lang.Nullable;

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
