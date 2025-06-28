package com.yetcache.example.service.loader;

import com.yetcache.core.kv.AbstractKVCacheLoader;
import com.yetcache.example.entity.User;
import com.yetcache.example.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Component
@Slf4j
public class IdKeyUserCacheLoader extends AbstractKVCacheLoader<Long, User> {
    @Autowired
    private IUserService userService;

    @Override
    public User load(Long bizKey) {
        return userService.getById(bizKey);
    }
}
