package cn.cuilan.bmp.controller;

import cn.cuilan.bmp.annotation.WebLog;
import cn.cuilan.bmp.entity.User;
import cn.cuilan.bmp.service.UserService;
import cn.cuilan.bmp.utils.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @WebLog("根据id查询用户")
    @GetMapping("/api/user/getUserById")
    public Results getUserById(long id) {
        return Results.success().data("user", userService.getUserById(id));
    }

    @WebLog(value = "根据用户名查询用户")
    @GetMapping("/api/user/getUsersByName")
    public Results getUsersByName(String name) {
        return Results.success().data("users", userService.getUsersByName(name));
    }

    @WebLog("获取全部用户")
    @GetMapping("/api/user/getAllUser")
    public Results getAllUser() {
        return Results.success().data("users", userService.getAllUser());
    }

}
