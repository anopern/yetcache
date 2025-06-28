package com.yetcache.core.mapper;

import com.yetcache.core.config.MultiTierCacheConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Mapper
public interface CacheConfigMapper {
    CacheConfigMapper INSTANCE = Mappers.getMapper(CacheConfigMapper.class);
    MultiTierCacheConfig clone(MultiTierCacheConfig origin);
}