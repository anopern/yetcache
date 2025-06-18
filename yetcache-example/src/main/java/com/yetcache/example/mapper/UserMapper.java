package com.yetcache.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yetcache.example.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
