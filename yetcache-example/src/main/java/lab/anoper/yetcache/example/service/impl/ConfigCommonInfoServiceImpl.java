package lab.anoper.yetcache.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lab.anoper.yetcache.example.domain.entity.ConfigCommonInfo;
import lab.anoper.yetcache.example.mapper.ConfigCommonInfoMapper;
import lab.anoper.yetcache.example.service.IConfigCommonInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
@Service
@Slf4j
public class ConfigCommonInfoServiceImpl extends ServiceImpl<ConfigCommonInfoMapper, ConfigCommonInfo>
        implements IConfigCommonInfoService {
    @Override
    public List<ConfigCommonInfo> listByTenantId(Long tenantId) {
        LambdaQueryWrapper<ConfigCommonInfo> queryWrapper = new LambdaQueryWrapper<ConfigCommonInfo>()
                .eq(ConfigCommonInfo::getTenantId, tenantId)
                .eq(ConfigCommonInfo::getDeleted, 0);
        return list(queryWrapper);
    }
}
