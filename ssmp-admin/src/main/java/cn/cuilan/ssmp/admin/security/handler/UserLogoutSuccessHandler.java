package cn.cuilan.ssmp.admin.security.handler;

import cn.cuilan.ssmp.utils.result.Result;
import cn.cuilan.ssmp.utils.result.ResultUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登出成功处理
 *
 * @author zhang.yan
 */
@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) {
        ResultUtil.responseJson(response, Result.success("登出成功."));
    }
}
