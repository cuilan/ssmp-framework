package cn.cuilan.ssmp.admin.controller;

import cn.cuilan.ssmp.admin.annotation.WebLog;
import cn.cuilan.ssmp.service.UserService;
import cn.cuilan.ssmp.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @WebLog("根据id查询用户")
    @GetMapping("/api/user/getUserById")
    public Result getUserById(long id) {
        return Result.map().data("user", userService.getNotNull(id));
    }

    @WebLog(value = "根据用户名查询用户")
    @GetMapping("/api/user/getUsersByName")
    public Result getUsersByName(String name) {
        return Result.map().data("users", userService.getUsersByName(name));
    }

    @WebLog("获取全部用户")
    @GetMapping("/api/user/getAllUser")
    public Result getAllUser(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return Result.map().data("users", userService.getAllUser(pageNum, pageSize));
    }

}
