package com.yetcache.example.assember;

import com.yetcache.example.base.common.cache.dto.UserDTO;
import com.yetcache.example.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    UserDTO doToDTO(User user);
}
