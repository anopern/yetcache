package com.yetcache.example.base.common.cache.agent;

import cn.hutool.core.lang.Assert;
import com.yetcache.agent.impl.AbstractKVCacheAgent;
import com.yetcache.properties.BaseCacheAgentProperties;
import com.yetcache.example.base.common.cache.dto.UserDTO;
import com.yetcache.source.IKVCacheSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UserCacheAgent extends AbstractKVCacheAgent<UserDTO> {
    public UserCacheAgent(@Qualifier("userCacheAgentProperties") @Autowired BaseCacheAgentProperties properties,
                          @Qualifier("userSourceService") @Autowired IKVCacheSourceService<UserDTO> sourceService) {
        super(properties, sourceService);
    }

    @Override
    public boolean isTenantScoped() {
        return false;
    }

    @Override
    protected String getBizKey(UserDTO dto) {
        Assert.notNull(dto.getId(), "用户ID不能为空！");
        return String.valueOf(dto.getId());
    }
}
