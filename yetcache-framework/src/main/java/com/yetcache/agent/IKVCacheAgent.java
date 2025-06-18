package com.yetcache.agent;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * @author walter.yan
 * @since 2025/5/21
 */
public interface IKVCacheAgent<E> extends ICacheAgent {
    E get(@NotNull String bizKey);

    E get(@Nullable Long tenantId, @NotNull String bizKey);

    void evictCache(@NotNull String bizKey);

    void evictCache(@Nullable Long tenantId, @NotNull String bizKey);

    void refreshCache(@NotNull String bizKey);

    void refreshCache(@Nullable Long tenantId, @NotNull String bizKey);
}
