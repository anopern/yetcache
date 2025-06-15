package lab.anoper.yetcache.example.controller;

import lab.anoper.yetcache.example.base.common.cache.agent.ConfigCommonInfoAgent;
import lab.anoper.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;
import lab.anoper.yetcache.tenant.TenantRequestContextHolder;
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
    private ConfigCommonInfoAgent configCommonInfoAgent;

    @PostMapping("/listAll")
    public List<ConfigCommonInfoDTO> listAll() {
        TenantRequestContextHolder.setCurTenantId(1002L);
        return configCommonInfoAgent.listAll();
    }

    @PostMapping("/get")
    public ConfigCommonInfoDTO get(@RequestBody ConfigCommonInfoDTO dto) {
        TenantRequestContextHolder.setCurTenantId(1002L);
        return configCommonInfoAgent.get(dto.getTenantId(), dto.getCode());
    }

}
