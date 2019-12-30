package cn.cuilan.ssmp.admin.security.handler;

import cn.cuilan.ssmp.utils.result.Result;
import cn.cuilan.ssmp.utils.result.ResultUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户登录失败处理
 *
 * @author zhang.yan
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) {
        // 用户不存在
        if (exception instanceof UsernameNotFoundException) {
            ResultUtil.responseJson(response, Result.userNotFound());
        }
        // 密码不正确
        if (exception instanceof BadCredentialsException) {
            ResultUtil.responseJson(response, Result.wrongPassword());
        }
        ResultUtil.responseJson(response, Result.fail("登录失败."));
    }
}
