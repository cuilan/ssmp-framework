package cn.cuilan.ssmp.admin.mvc;

import org.junit.jupiter.api.Test;

public class UserControllerTest extends BaseMvcTest {

    private String token = "1";

    @Test
    public void testGetUserById() {
        String uri = "/api/user/getUserById";
        getMock(uri)
                .token(token)
                .param("id", 1L)
                .execute()
                .andExpectCode(200);
    }

}
