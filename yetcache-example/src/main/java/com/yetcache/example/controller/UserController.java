package com.yetcache.example.controller;

import com.yetcache.example.cache.UserCacheAgent;
import com.yetcache.example.entity.User;
import lombok.AllArgsConstructor;
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
    private UserCacheAgent userCacheAgent;

    @PostMapping("/getById")
    public User getById(@RequestBody User dto) {
        return userCacheAgent.getById(dto.getId());
    }
}
