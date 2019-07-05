package cn.cuilan.bmp.mapper;

import cn.cuilan.bmp.entity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserMapper userMapper;

    private long userId;

    @Before
    public void before() {
        User user = new User("test", "pass", 24);
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
