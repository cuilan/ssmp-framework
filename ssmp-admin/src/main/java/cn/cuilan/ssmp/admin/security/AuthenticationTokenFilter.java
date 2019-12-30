package cn.cuilan.ssmp.admin.security;

import cn.cuilan.ssmp.Constants;
import cn.cuilan.ssmp.admin.security.service.SysUserDetailsServiceImpl;
import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 从cookie中获取token进行验证
 *
 * @author zhang.yan
 */
public class AuthenticationTokenFilter extends BasicAuthenticationFilter {

    public final static String UID_KEY = "UID";

    @Resource
    private SysUserDetailsServiceImpl sysUserDetailsServiceImpl;

    public AuthenticationTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        // 获取cookie
        Cookie cookie = ServletUtil.getCookie(request, Constants.COOKIE_NAME_SYS_USER);
        // TODO

        filterChain.doFilter(request, response);
    }
}
