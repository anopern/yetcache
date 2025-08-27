package com.yetcache.example.service.loader;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.agent.core.structure.kv.loader.AbstractKvCacheLoader;
import com.yetcache.example.entity.User;
import com.yetcache.example.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Component
@Slf4j
public class IdKeyUserCacheLoader extends AbstractKvCacheLoader<Long, User> {
    @Autowired
    private IUserService userService;

    @Override
    public User load(Long bizKey) {
        return userService.getById(bizKey);
    }

    @Override
    public Map<Long, User> batchLoad(List<Long> bizKeys) {
        List<User> users = userService.list(bizKeys);
        if (CollUtil.isEmpty(users)) {
            return new HashMap<>();
        }
        return users.stream().collect(HashMap::new, (m, v) -> m.put(v.getId(), v), HashMap::putAll);
    }
}
