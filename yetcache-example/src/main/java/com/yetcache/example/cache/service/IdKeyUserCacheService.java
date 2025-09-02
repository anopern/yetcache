package com.yetcache.example.cache.service;

import com.yetcache.agent.agent.kv.BaseKvCacheAgent;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.example.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@Service
@Slf4j
public class IdKeyUserCacheService {
    @Qualifier("idKeyUserCacheAgent")
    @Autowired
    private BaseKvCacheAgent idKeyUserCacheAgent;

    public User get(Long id) {
        BaseCacheResult<User> result = idKeyUserCacheAgent.get(id);
        if (null != result && result.isSuccess()) {
            return result.value();
        }
        return null;
    }
}
