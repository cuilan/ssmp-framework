package cn.cuilan.bmp.service;

import cn.cuilan.bmp.mapper.UserMapper;
import cn.cuilan.bmp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private UserMapper userMapper;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 根据id查询用户
     *
     * @param id 用户id
     * @return user对象
     */
    public User getUserById(long id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据真实姓名查询用户
     *
     * @param name 真实姓名
     * @return 返回多个名称相同的用户集合
     */
    public List<User> getUsersByName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("real_name", name);
        return userMapper.selectByMap(map);
    }

    /**
     * 获取全部用户，仅供参数使用，慎用
     *
     * @return 返回当前数据库表中所有的数据
     */
    @Deprecated
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }
}
