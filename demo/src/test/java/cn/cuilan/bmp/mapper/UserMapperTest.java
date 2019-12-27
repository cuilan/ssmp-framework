package cn.cuilan.bmp.mapper;

import cn.cuilan.entity.User;
import cn.cuilan.enums.Gender;
import cn.cuilan.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    private long userId;

    @Before
    public void before() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456");
        user.setRealName("管理员");
        user.setGender(Gender.MAN);
        user.setAge(24);
        Integer result = userMapper.insert(user);
        Assert.assertTrue(result == 1);
        userId = user.getId();
        System.out.println(userId);
    }

    @Test
    public void testSelectById() {
        User user = userMapper.selectById(userId);
        System.out.println(user);
    }

}
