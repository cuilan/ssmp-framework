package cn.cuilan.ssmp.admin.security.handler;

import cn.cuilan.ssmp.Constants;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.mapper.SysUserMapper;
import cn.cuilan.ssmp.redis.RedisUtils;
import cn.cuilan.ssmp.redis.SysUserRedisPrefix;
import cn.cuilan.ssmp.utils.NetworkUtils;
import cn.cuilan.ssmp.utils.UuidUtils;
import cn.cuilan.ssmp.utils.result.Result;
import cn.cuilan.ssmp.utils.result.ResultUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录成功后的处理
 */
@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${spring.profiles.active}")
    private String profile;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        String ipAddress = NetworkUtils.getIpAddress(request);

        SysUser sysUser = (SysUser) authentication.getPrincipal();
        String token;
        if ("prod".equals(profile)) {
            token = UuidUtils.createUuid();
            redisUtils.saveString(SysUserRedisPrefix.TOKEN, token, String.valueOf(sysUser.getId()));
            // 写入cookie
            ServletUtil.addCookie(response, new Cookie(Constants.ADMIN_COOKIE_NAME, token));
        } else {
            token = String.valueOf(sysUser.getId());
            // 写入cookie
            ServletUtil.addCookie(response, new Cookie(Constants.TEST_ADMIN_COOKIE_NAME, token));
        }
        SysUser dbSysUser = sysUserMapper.selectById(sysUser.getId());
        dbSysUser.setRoles(sysUser.getRoles());
        dbSysUser.setPermissions(sysUser.getPermissions());
        dbSysUser.setMenus(sysUser.getMenus());
        dbSysUser.setLastLoginIp(ipAddress);
        dbSysUser.setLastLoginTime(System.currentTimeMillis());
        sysUserMapper.updateById(dbSysUser);
        log.info("管理员 [{}] 登录系统, IP: [{}]", sysUser.getUsername(), ipAddress);


        ResultUtil.responseJson(response, Result.success("登录成功", dbSysUser));
    }
}
