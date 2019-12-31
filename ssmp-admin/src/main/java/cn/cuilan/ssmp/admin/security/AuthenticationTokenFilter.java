package cn.cuilan.ssmp.admin.security;

import cn.cuilan.ssmp.Constants;
import cn.cuilan.ssmp.admin.security.domain.SysUserDetails;
import cn.cuilan.ssmp.admin.security.service.SysUserDetailsServiceImpl;
import cn.cuilan.ssmp.redis.RedisUtils;
import cn.cuilan.ssmp.redis.SysUserRedisPrefix;
import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * 从cookie中获取token进行验证
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
public class AuthenticationTokenFilter extends BasicAuthenticationFilter {

    @Value("${spring.profiles.active}")
    private String profile;

    @Resource
    private SysUserDetailsServiceImpl sysUserDetailsServiceImpl;

    @Resource
    private RedisUtils redisUtils;

    public AuthenticationTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        // 获取cookie
        Cookie cookie;
        if ("prod".equals(profile)) {
            // 生产环境获取正式token
            cookie = ServletUtil.getCookie(request, Constants.ADMIN_COOKIE_NAME);
        } else {
            // 非生产环境获取测试token
            cookie = ServletUtil.getCookie(request, Constants.TEST_ADMIN_COOKIE_NAME);
        }

        if (cookie != null) {
            String token = cookie.getValue();
            String userId;
            // 非prod环境，且token是以：UID为起始，为测试方便
            if ("prod".equals(profile)) {
                // 正式环境从redis中获取token的userId
                userId = redisUtils.getString(SysUserRedisPrefix.TOKEN, token);
            } else {
                userId = token;
            }

            if (userId != null) {
                Long uid = Long.parseLong(userId);
                SysUserDetails sysUserDetails = sysUserDetailsServiceImpl.getSysUserInfo(uid, null);
                if (sysUserDetails != null) {
                    // 获取角色和权限
                    Collection<? extends GrantedAuthority> authorities = sysUserDetails.getAuthorities();
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(sysUserDetails, uid, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
