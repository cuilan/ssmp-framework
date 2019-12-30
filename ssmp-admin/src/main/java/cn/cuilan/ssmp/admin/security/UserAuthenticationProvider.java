package cn.cuilan.ssmp.admin.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    // TODO
    //@Resource
    //private SysUserDetailsService sysUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        // TODO loadUserByUsername(username);

        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
