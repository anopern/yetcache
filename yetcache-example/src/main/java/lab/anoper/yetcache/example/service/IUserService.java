package lab.anoper.yetcache.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import lab.anoper.yetcache.example.domain.entity.User;

public interface IUserService extends IService<User> {
    User getById(Long id);
}
