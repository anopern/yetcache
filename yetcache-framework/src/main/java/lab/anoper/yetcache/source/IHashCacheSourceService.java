package lab.anoper.yetcache.source;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 针对多Hash缓存的数据源查询接口
 *
 * @author walter.yan
 * @since 2025/4/27
 */
public interface IHashCacheSourceService<E> {
    List<E> queryAll(@Nullable Long tenantId);

    List<E> queryPage(@Nullable Long tenantId, @NotNull Integer pageNo, @NotNull Integer pageSize);

    E querySingle(@Nullable Long tenantId, @NotNull String bizKey, @NotNull String bizHashKey);

    List<E> queryList(@Nullable Long tenantId, @NotNull String bizKey);

    List<E> queryList(@Nullable Long tenantId, @NotNull List<String> bizKeys);

    List<E> queryList(@Nullable Long tenantId, @NotNull Map<String, List<String>> bizKeyMap);
}