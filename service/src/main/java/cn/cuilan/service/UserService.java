package cn.cuilan.service;

import cn.cuilan.entity.User;
import cn.cuilan.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        return userMapper.getByRealName(realName);
    }

    /**
     * 获取全部用户，仅供参数使用，慎用
     *
     * @return 返回当前数据库表中所有的数据
     */
    public List<User> getAllUser(int pageNum, int pageSize) {
        return userMapper.getAllUser(pageNum, pageSize);
    }
}
