package lab.anoper.yetcache.example.assember;

import lab.anoper.yetcache.example.base.common.cache.dto.UserDTO;
import lab.anoper.yetcache.example.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    UserDTO doToDTO(User user);
}
