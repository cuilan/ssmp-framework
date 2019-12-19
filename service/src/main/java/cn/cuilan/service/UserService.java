package cn.cuilan.service;

import cn.cuilan.entity.User;
import cn.cuilan.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<UserMapper, User> {
}
