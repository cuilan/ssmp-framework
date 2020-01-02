package cn.cuilan.ssmp.admin.controller;

import cn.cuilan.ssmp.Constants;
import cn.cuilan.ssmp.admin.annotation.Logined;
import cn.cuilan.ssmp.admin.form.SysUserAddForm;
import cn.cuilan.ssmp.admin.form.SysUserUpdateForm;
import cn.cuilan.ssmp.admin.security.service.SysUserDetailsServiceImpl;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.enums.SysUserStatusEnum;
import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.redis.RedisUtils;
import cn.cuilan.ssmp.redis.SysUserRedisPrefix;
import cn.cuilan.ssmp.service.SysUserService;
import cn.cuilan.ssmp.utils.result.Result;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Slf4j
public class SysUserController {

    @Value("${spring.profiles.active}")
    private String profile;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysUserDetailsServiceImpl sysUserDetailsService;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private RedisUtils redisUtils;

    @PostMapping("/api/admin/sysuser/islogin")
    @PreAuthorize("authenticated")
    public Result getCurrentSysUser(@Logined SysUser sysUser) {
        return Result.success(sysUser);
    }

    @GetMapping("/api/admin/sysuser/query")
    @PreAuthorize("authenticated and hasPermission('user', 'query')")
    public Result getSysUsers(@RequestParam(value = "uid", required = false) Long userId,
                              @RequestParam(value = "userName", required = false) String userName,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "30") Integer pageSize) {

        return Result.success(sysUserService.getSysUser(userId, userName, status, pageNum, pageSize));
    }

    /**
     * 超管添加其他系统用户
     */
    @PostMapping("/api/admin/sysuser/add")
    @PreAuthorize("authenticated and hasPermission('user', 'add')")
    public Result addSysUser(@Logined SysUser sysUser, @Valid SysUserAddForm form) {
        SysUser newSysUser = new SysUser();
        newSysUser.setUsername(form.getUsername());
        String tmpPwd = sysUserService.genTmpPwd();
        newSysUser.setTmpPwd(tmpPwd);
        newSysUser.setPassword(bCryptPasswordEncoder.encode(tmpPwd));
        newSysUser.setFullName(form.getFullName());
        newSysUser.setReservedInfo(form.getReservedInfo());
        newSysUser.setPhone(form.getPhone());
        newSysUser.setNotes(form.getNotes());
        newSysUser.setCreatedBy(sysUser.getId());
        newSysUser.setStatus(SysUserStatusEnum.ACTIVATED.getValue());

        return Result.success(sysUserService.addSysUser(newSysUser, form.getRoles()));
    }

    /**
     * 修改用户信息
     */
    @PostMapping("/api/admin/sysuser/update")
    @PreAuthorize("authenticated and hasPermission('user', 'update')")
    public Result updateSysUser(@Logined SysUser sysUser, @Valid SysUserUpdateForm form) {
        if (sysUser.getId().equals(form.getUid()) && form.getStatus() != null) {
            throw new BaseException("不能修改自己的用户状态");
        }
        SysUser targetSysUser = sysUserService.getNotNull(form.getUid());
        if (targetSysUser.getId() == 1L && form.getStatus() != null) {
            throw new BaseException("不能修改超管的状态");
        }
        if (form.getStatus() != null) {
            targetSysUser.setStatus(form.getStatus());
        }
        if (form.getEmail() != null) {
            targetSysUser.setEmail(form.getEmail());
        }
        if (form.getPhone() != null) {
            targetSysUser.setPhone(form.getPhone());
        }
        if (form.getFullName() != null) {
            targetSysUser.setFullName(form.getFullName());
        }
        if (form.getNotes() != null) {
            targetSysUser.setNotes(form.getNotes());
        }
        if (form.getRoles() == null) {
            sysUserService.updateSysUser(targetSysUser);
        } else {
            sysUserService.updateSysUser(targetSysUser, form.getRoles());
        }

        return Result.success("修改成功");
    }

    /**
     * 用户修改自己的密码
     */
    @PostMapping("/api/admin/sysuser/reset/pass")
    @ResponseBody
    @PreAuthorize("authenticated")
    public Result resetPassword(HttpServletRequest request,
                                @RequestParam("old") String oldPass,
                                @RequestParam("new") String newPass,
                                @Logined SysUser sysUser) {
        sysUserDetailsService.resetPassword(sysUser, oldPass, newPass);
        // 删除redis key，强制退出
        Cookie cookie;
        if ("prod".equals(profile)) {
            cookie = ServletUtil.getCookie(request, Constants.ADMIN_COOKIE_NAME);
        } else {
            cookie = ServletUtil.getCookie(request, Constants.TEST_ADMIN_COOKIE_NAME);
        }
        String token = cookie.getValue();
        if (redisUtils.deleteKey(SysUserRedisPrefix.TOKEN, token)) {
            return Result.success("修改成功，请重新登录");
        }
        return Result.fail("修改失败，请重新尝试");
    }
}
