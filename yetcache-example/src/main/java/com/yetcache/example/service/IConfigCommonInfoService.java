package com.yetcache.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yetcache.example.domain.entity.ConfigCommonInfo;
import com.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
public interface IConfigCommonInfoService extends IService<ConfigCommonInfo> {
    List<ConfigCommonInfo> listByTenantId(Long tenantId);

    void updateOrCreate(ConfigCommonInfoDTO dto);
}
