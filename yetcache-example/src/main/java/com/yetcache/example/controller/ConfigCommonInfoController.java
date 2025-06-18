package com.yetcache.example.controller;

import cn.hutool.core.util.StrUtil;
import com.yetcache.example.base.common.cache.agent.ConfigCommonInfoCacheAgent;
import com.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;
import com.yetcache.example.service.IConfigCommonInfoService;
import com.yetcache.tenant.TenantRequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
@Slf4j
@RestController
@RequestMapping("/api/configCommonInfos")
public class ConfigCommonInfoController {
    @Autowired
    private ConfigCommonInfoCacheAgent configCommonInfoCacheAgent;
    @Autowired
    private IConfigCommonInfoService configCommonInfoService;

    @PostMapping("/listAll")
    public List<ConfigCommonInfoDTO> listAll() {
        TenantRequestContextHolder.setCurTenantId(1002L);
        return configCommonInfoCacheAgent.listAll();
    }

    @PostMapping("/get")
    public ConfigCommonInfoDTO get(@RequestBody ConfigCommonInfoDTO dto) {
        TenantRequestContextHolder.setCurTenantId(1002L);
        return configCommonInfoCacheAgent.get(dto.getTenantId(), dto.getCode());
    }

    @PostMapping("/updateOrCreate")
    public String updateOrCreate(@RequestBody ConfigCommonInfoDTO dto) {
        if (dto.getTenantId() == null || StrUtil.isBlank(dto.getCode()) ||
                StrUtil.isBlank(dto.getValue())) {
            return "参数错误";
        }
        configCommonInfoService.updateOrCreate(dto);
        return "";
    }

    @PostMapping("/refreshAllCache")
    public String refreshAllCache(@RequestBody ConfigCommonInfoDTO dto) {
        if (dto.getTenantId() == null) {
            return "参数错误";
        }
        configCommonInfoCacheAgent.refreshAllCache(dto.getTenantId());
        return "";
    }

}
