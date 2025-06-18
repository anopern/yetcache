package com.yetcache.example.source;

import com.yetcache.example.assember.UserConverter;
import com.yetcache.example.service.IUserService;
import com.yetcache.example.base.common.cache.dto.UserDTO;
import com.yetcache.example.domain.entity.User;
import com.yetcache.source.impl.AbstractKVCacheSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userSourceService")
public class UserCacheSourceService extends AbstractKVCacheSourceService<UserDTO> {
    @Autowired
    private IUserService userService;

    @Override
    public UserDTO querySingle(Long tenantId, String bizKey) {
        Long userId = Long.parseLong(bizKey);
        User user = userService.getById(userId);
        return UserConverter.INSTANCE.doToDTO(user);
    }
}
