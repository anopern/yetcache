package lab.anoper.yetcache.example.source;

import lab.anoper.yetcache.example.assember.UserConverter;
import lab.anoper.yetcache.example.base.common.cache.dto.UserDTO;
import lab.anoper.yetcache.example.domain.entity.User;
import lab.anoper.yetcache.example.service.IUserService;
import lab.anoper.yetcache.source.impl.AbstractKVCacheSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userSourceService")
public class UserCacheSourceService extends AbstractKVCacheSourceService<UserDTO> {
    @Autowired
    private IUserService userService;

    @Override
    public UserDTO querySingle(Long tenantId, String bizKey) {
        Long userId = Long.parseLong(bizKey);
        User user = userService.getById(userId);
        return UserConverter.INSTANCE.doToDTO(user);
    }
}
