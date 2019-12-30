package cn.cuilan.ssmp.admin.mvc;

import org.junit.Test;

public class UserControllerTest extends BaseMvcTest {

    @Test
    public void testGetUserById() {
        String uri = "/api/user/getUserById";
        getMock(uri)
                .param("id", 1L)
                .execute()
                .andExpectCode(200);
    }

}
