package cn.cuilan.ssmp.admin.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * 自定义权限拦截
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
public class CustomPermissionEvaluator implements PermissionEvaluator {

    /**
     * 是否开启权限验证
     */
    @Value("${ssmp.enablePermission}")
    private boolean enablePermission;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permission) {
        // 测试环境关闭权限验证
        if (!enablePermission) {
            return true;
        }
        String permissionInfo = String.format("%s_%s", targetObject.toString(), permission.toString());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(permissionInfo)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String type, Object permission) {
        return false;
    }
}
