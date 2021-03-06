package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.entity.User;
import cn.cuilan.ssmp.enums.Gender;
import cn.cuilan.ssmp.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class UserService extends BaseService<UserMapper, User> {

    @Resource
    private UserMapper userMapper;

    /**
     * 根据真实姓名查询用户
     *
     * @param realName 真实姓名
     * @return 返回多个名称相同的用户集合
     */
    public User getUsersByName(String realName) {
        return userMapper.getByUsername(realName);
    }

    /**
     * 获取全部用户，仅供参数使用，慎用
     *
     * @return 返回当前数据库表中所有的数据
     */
    public List<User> getAllUser(int pageNum, int pageSize) {
        return userMapper.getAllUser(pageNum, pageSize);
    }

    public void saveAll() {
        User user = new User();
        user.setUsername("aaa");
        user.setPassword("123");
        user.setRealName("aaa");
        user.setGender(Gender.MAN);
        user.setAge(18);
        userMapper.saveBatch(Collections.singletonList(user));
    }
}
