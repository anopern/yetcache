package com.yetcache.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yetcache.example.domain.entity.User;

public interface IUserService extends IService<User> {
    User getById(Long id);
}
