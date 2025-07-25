package com.yetcache.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yetcache.example.entity.User;
import com.yetcache.example.mapper.UserMapper;
import com.yetcache.example.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements IUserService {
    public User getById(Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getId, id)
                .eq(User::getDeleted, 0);
        return getOne(queryWrapper);
    }

    @Override
    public List<User> list(List<Long> ids) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .in(User::getId, ids)
                .eq(User::getDeleted, 0);
        return list(queryWrapper);
    }
}
