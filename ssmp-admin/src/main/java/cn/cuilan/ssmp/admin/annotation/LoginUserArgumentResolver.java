package cn.cuilan.ssmp.admin.annotation;

import cn.cuilan.ssmp.admin.security.domain.SysUserDetails;
import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.utils.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Logined 注解参数解析器
 *
 * @author zhang.yan
 */
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Logined methodAnnotation = methodParameter.getMethodAnnotation(Logined.class);
        if (methodAnnotation != null) {
            return true;
        }
        Logined loginUser = methodParameter.getParameterAnnotation(Logined.class);
        return loginUser != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        // 获取当前登录用户
        Object sysUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (sysUser instanceof SysUserDetails) {
            return sysUser;
        }
        // 未登录
        throw new BaseException(Result.NO_AUTH.getValue(), "请登录后重试");
    }
}
