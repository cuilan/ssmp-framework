package cn.cuilan.ssmp.admin.security.handler;

import cn.cuilan.ssmp.utils.result.Result;
import cn.cuilan.ssmp.utils.result.ResultUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户认证入口，全部处理为未登录，使用自定义的过滤器处理登录验证
 *
 * @author zhang.yan
 */
@Component
public class UserAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception) {
        ResultUtil.responseJson(response, Result.fail(exception.getMessage()));
    }
}
