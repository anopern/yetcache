package com.yetcache.example.service.loader;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.core.cache.loader.AbstractFlatHashCacheLoader;
import com.yetcache.example.entity.ConfigCommonInfo;
import com.yetcache.example.service.IConfigCommonInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
@Component
public class ConfigCommonInfoCacheLoader extends AbstractFlatHashCacheLoader<String, String, ConfigCommonInfo> {
    @Autowired
    private IConfigCommonInfoService configCommonInfoService;

    @Override
    public ConfigCommonInfo load(String bizKey, String bizField) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ConfigCommonInfo> loadAll(String bizKey) {
        List<ConfigCommonInfo> list = configCommonInfoService.listAll();
        if (CollUtil.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(ConfigCommonInfo::getCode, Function.identity()));
    }
}
