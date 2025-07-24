package com.yetcache.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yetcache.example.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Mapper
public interface UserMapper extends BaseMapper<User>{
}
