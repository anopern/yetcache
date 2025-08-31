package com.yetcache.example.service.loader;

import com.yetcache.agent.agent.kv.loader.AbstractKvCacheLoader;
import com.yetcache.agent.agent.kv.loader.KvCacheLoadCommand;
import com.yetcache.core.result.*;
import com.yetcache.example.domain.entity.User;
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
public class IdKeyUserCacheLoader extends AbstractKvCacheLoader<Long, User> {
    @Autowired
    private IUserService userService;

    @Override
    public String getLoaderName() {
        return "id-key-user-cache-loader";
    }

    @Override
    public BaseCacheResult<User> load(KvCacheLoadCommand<Long> cmd) {
        Long userId = cmd.getBizKey();
        User user = userService.getById(userId);
        return BaseCacheResult.singleHit(getLoaderName(), user, HitLevel.SOURCE, FreshnessInfo.fresh());
    }
}
