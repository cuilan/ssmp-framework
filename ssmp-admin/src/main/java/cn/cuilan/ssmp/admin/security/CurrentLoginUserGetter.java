package cn.cuilan.ssmp.admin.security;

import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.exception.BaseException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 获取当前登录用户
 */
@Component
public class CurrentLoginUserGetter {

    /**
     * 获取当前登录用户
     *
     * @return 返回当前登录用户
     */
    public SysUser getCurrentLoginUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            throw new BaseException("未登录");
        }
        return (SysUser) principal;
    }
}
