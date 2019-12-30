package cn.cuilan.ssmp.admin.service;

import cn.cuilan.ssmp.admin.AdminApplicationTests;
import cn.cuilan.ssmp.entity.User;
import cn.cuilan.ssmp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Rollback
@Transactional
@Slf4j
public class UserServiceTest extends AdminApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("根据用户名查询用户")
    public void testGetUsersByName() {
        User user = userService.getUsersByName("admin");
        Assert.assertNotNull(user);
    }

}
