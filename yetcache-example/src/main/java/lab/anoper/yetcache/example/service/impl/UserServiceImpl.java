package lab.anoper.yetcache.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lab.anoper.yetcache.example.domain.entity.User;
import lab.anoper.yetcache.example.mapper.UserMapper;
import lab.anoper.yetcache.example.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements IUserService {
    public User getById(Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getId, id)
                .eq(User::getDeleted, 0);
        return getOne(queryWrapper);
    }
}
