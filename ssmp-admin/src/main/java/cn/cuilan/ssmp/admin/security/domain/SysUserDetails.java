package cn.cuilan.ssmp.admin.security.domain;

import cn.cuilan.ssmp.entity.SysPermission;
import cn.cuilan.ssmp.entity.SysRole;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.exception.BaseException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserDetails extends SysUser implements UserDetails {

    public SysUserDetails() {
    }

    public SysUserDetails(SysUser sysUser) {
        if (sysUser == null) {
            throw new UsernameNotFoundException("未找到该用户");
        }
        this.id = sysUser.getId();
        this.phone = sysUser.getPhone();
        this.email = sysUser.getEmail();
        this.username = sysUser.getUsername();
        this.password = sysUser.getPassword();
        this.isAdmin = sysUser.isAdmin();
        this.status = sysUser.getStatus();
        this.fullName = sysUser.getFullName();
        this.portrait = sysUser.getPortrait();
        this.reservedInfo = sysUser.getReservedInfo();
        this.tmpPwd = sysUser.getTmpPwd();
        this.trashTmpPwd = sysUser.isTrashTmpPwd();
        this.notes = sysUser.getNotes();
        this.createdBy = sysUser.getCreatedBy();
        this.lastLoginIp = sysUser.getLastLoginIp();
        this.lastLoginTime = sysUser.getLastLoginTime();
        this.createTime = sysUser.getCreateTime();
        this.updateTime = sysUser.getUpdateTime();
        this.roles = sysUser.getRoles();
        this.permissions = sysUser.getPermissions();
        this.menus = sysUser.getMenus();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<>();
        // 获取并设置角色和权限
        if (this.roles != null && (this.roles).size() > 0) {
            for (SysRole sysRole : this.roles) {
                auths.add(new SimpleGrantedAuthority(sysRole.getName()));
            }
        }
        if (this.permissions != null && (this.permissions).size() > 0) {
            for (SysPermission sysPermission : this.permissions) {
                auths.add(new SimpleGrantedAuthority(sysPermission.getName()));
            }
        }
        return auths;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
