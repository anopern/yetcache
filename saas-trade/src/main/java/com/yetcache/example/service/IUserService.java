package com.yetcache.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yetcache.example.entity.User;

import java.util.List;

public interface IUserService extends IService<User> {
    User getById(Long id);

    List<User> list(List<Long> ids);
}
