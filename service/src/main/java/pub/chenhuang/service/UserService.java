package pub.chenhuang.service;

import org.springframework.stereotype.Service;
import pub.chenhuang.mapper.UserMapper;
import pub.chenhuang.pojo.User;

import javax.annotation.Resource;

/**
 * Created by ch on 2017/6/19.
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public User getUserByName(String name){
        return userMapper.findByName(name);
    }
}
