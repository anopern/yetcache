package lab.anoper.yetcache.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lab.anoper.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;
import lab.anoper.yetcache.example.domain.entity.ConfigCommonInfo;
import lab.anoper.yetcache.example.mapper.ConfigCommonInfoMapper;
import lab.anoper.yetcache.example.service.IConfigCommonInfoService;
import lab.anoper.yetcache.utils.TransactionalEventUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
@Service
@Slf4j
public class ConfigCommonInfoServiceImpl extends ServiceImpl<ConfigCommonInfoMapper, ConfigCommonInfo>
        implements IConfigCommonInfoService {
    @Autowired
    private TransactionalEventUtils transactionalEventUtils;

    @Override
    public List<ConfigCommonInfo> listByTenantId(Long tenantId) {
        LambdaQueryWrapper<ConfigCommonInfo> queryWrapper = new LambdaQueryWrapper<ConfigCommonInfo>()
                .eq(ConfigCommonInfo::getTenantId, tenantId)
                .eq(ConfigCommonInfo::getDeleted, 0);
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public void updateOrCreate(ConfigCommonInfoDTO dto) {
        if (!updateByTenantIdAndCode(dto)) {
            ConfigCommonInfo entity = new ConfigCommonInfo();
            entity.setTenantId(dto.getTenantId());
            entity.setCode(dto.getCode());
            entity.setValue(dto.getValue());
            save(entity);
        }
        publishConfigCommonInfoEvent(dto.getTenantId(), dto.getCode());
    }

    private boolean updateByTenantIdAndCode(ConfigCommonInfoDTO dto) {
        ConfigCommonInfo entity = new ConfigCommonInfo();
        entity.setValue(dto.getValue());
        entity.setUpdatedTime(LocalDateTime.now());
        LambdaUpdateWrapper<ConfigCommonInfo> updateWrapper = new LambdaUpdateWrapper<ConfigCommonInfo>()
                .eq(ConfigCommonInfo::getTenantId, dto.getTenantId())
                .eq(ConfigCommonInfo::getCode, dto.getCode());
        return update(entity, updateWrapper);
    }

    private void publishConfigCommonInfoEvent(Long tenantId, String code) {
        ConfigCommonInfo entity = new ConfigCommonInfo();
        entity.setTenantId(tenantId);
        entity.setCode(code);
        transactionalEventUtils.publishAfterCommit(entity);
    }
}
