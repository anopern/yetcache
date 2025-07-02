//package com.yetcache.example.controller;
//
//import com.yetcache.example.cache.ConfigCommonInfoCacheAgent;
//import com.yetcache.example.entity.ConfigCommonInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author walter.yan
// * @since 2025/7/1
// */
//@RestController
//@RequestMapping("/api/config-common-infos")
//public class ConfigCommonInfoController {
//    @Autowired
//    private ConfigCommonInfoCacheAgent agent;
//
//    @PostMapping("/get")
//    public ConfigCommonInfo get(@RequestBody ConfigCommonInfo dto) {
//        return agent.getByCode(dto.getCode());
//    }
//}
