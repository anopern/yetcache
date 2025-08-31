//package com.yetcache.example.service.loader;
//
//import cn.hutool.core.collection.CollUtil;
//import com.yetcache.agent.core.structure.config.FlatHashCacheLoader;
//import com.yetcache.example.domain.entity.ConfigCommonInfo;
//import com.yetcache.example.service.IConfigCommonInfoService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * @author walter.yan
// * @since 2025/7/1
// */
//@Component
//public class ConfigCommonInfoCacheLoader implements FlatHashCacheLoader<String, ConfigCommonInfo> {
//    @Autowired
//    private IConfigCommonInfoService configCommonInfoService;
//
//
//    @Override
//    public Map<String, ConfigCommonInfo> loadAll() {
//        List<ConfigCommonInfo> list = configCommonInfoService.listAll();
//        if (CollUtil.isEmpty(list)) {
//            return new HashMap<>();
//        }
//        return list.stream().collect(Collectors.toMap(ConfigCommonInfo::getCode, Function.identity()));
//    }
//}
