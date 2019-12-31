package cn.cuilan.ssmp.admin.security;

import cn.cuilan.ssmp.admin.security.domain.SysUserDetails;
import cn.cuilan.ssmp.admin.security.service.SysUserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private SysUserDetailsServiceImpl sysUserDetailsServiceImpl;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        SysUserDetails loginUser = (SysUserDetails) sysUserDetailsServiceImpl.loadUserByUsername(username);
        // 校验密码是否正确
        sysUserDetailsServiceImpl.checkPassword(loginUser, password);
        return new UsernamePasswordAuthenticationToken(loginUser, password, loginUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
