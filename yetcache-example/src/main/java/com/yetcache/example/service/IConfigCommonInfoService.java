package com.yetcache.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yetcache.example.entity.ConfigCommonInfo;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
public interface IConfigCommonInfoService extends IService<ConfigCommonInfo> {
    List<ConfigCommonInfo> listAll();

//    void updateOrCreate(ConfigCommonInfo dto);
}
