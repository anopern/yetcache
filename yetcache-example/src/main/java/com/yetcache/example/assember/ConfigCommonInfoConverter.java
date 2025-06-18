package com.yetcache.example.assember;

import com.yetcache.example.domain.entity.ConfigCommonInfo;
import com.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
@Mapper
public interface ConfigCommonInfoConverter {
    ConfigCommonInfoConverter INSTANCE = Mappers.getMapper(ConfigCommonInfoConverter.class);

    List<ConfigCommonInfoDTO> doListToDTOList(List<ConfigCommonInfo> list);
}
