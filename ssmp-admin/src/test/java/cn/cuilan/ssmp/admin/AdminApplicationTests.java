package cn.cuilan.ssmp.admin;

import cn.cuilan.ssmp.entity.User;

import cn.cuilan.ssmp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AdminApplicationTests {

    protected User user;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUpTests() {
        user = userService.getNotNull(1L);
    }

}
