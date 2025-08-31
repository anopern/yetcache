package com.yetcache.example.controller;

import com.yetcache.example.cache.service.IdKeyUserCacheService;
import com.yetcache.example.domain.entity.User;
import com.yetcache.example.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    @Autowired
    private IdKeyUserCacheService idKeyUserCacheService;

    @PostMapping("/getById")
    public R<User> getById(@RequestBody User dto) {
        return R.ok(idKeyUserCacheService.get(dto.getId()));
    }
}
