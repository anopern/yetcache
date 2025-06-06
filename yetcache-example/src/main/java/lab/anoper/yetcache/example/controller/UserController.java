package lab.anoper.yetcache.example.controller;

import lab.anoper.yetcache.example.base.common.cache.agent.UserCacheAgent;
import lab.anoper.yetcache.example.base.common.cache.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserCacheAgent userCacheAgent;

    @RequestMapping("/getUserById")
    public UserDTO getUserById(@RequestParam Long id) {
        return userCacheAgent.get(String.valueOf(id));
    }
}
