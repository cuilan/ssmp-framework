package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.mapper.SysMenuRolesMapper;
import cn.cuilan.ssmp.mapper.SysPermissionRolesMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.mapper.SysUserRolesMapper;
import cn.cuilan.ssmp.utils.RoleAndPermissionNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysMenuRolesMapper sysMenuRolesMapper;

    @Resource
    private SysUserRolesMapper sysUserRolesMapper;

    @Resource
    private SysPermissionRolesMapper sysPermissionRolesMapper;

    public List<SysRole> getCurrentUserRoles(SysUser sysUser) {
        return sysRoleMapper.getCurrentUserRoles(sysUser.getId());
    }

    public List<SysRole> getSysRoles(Long sysRoleId) {
        if (sysRoleId != null && sysRoleId > 0) {
            return Collections.singletonList(sysRoleMapper.selectById(sysRoleId));
        }
        return sysRoleMapper.getAllSysRoles();
    }

    public boolean addSysRole(SysRole sysRole) {
        if (StringUtils.isBlank(sysRole.getName())) {
            throw new BaseException("角色名称必填");
        }
        RoleAndPermissionNameUtils.checkName(sysRole.getName());
        if (!sysRoleMapper.exsitRoleName(sysRole.getName())) {
            throw new BaseException("该角色已存在");
        }
        sysRole.setVisible(true);
        return sysRoleMapper.insert(sysRole) == 1;
    }

    public boolean update(SysRole sysRole) {
        if (sysRole.getId() == null || sysRole.getId() == 0) {
            throw new BaseException("角色id不能为空");
        }
        RoleAndPermissionNameUtils.checkName(sysRole.getName());
        if (!sysRoleMapper.exsitRoleName(sysRole.getName())) {
            throw new BaseException("该角色已存在");
        }
        return sysRoleMapper.updateById(sysRole) == 1;
    }

    public boolean removeSysRole(long sysRoleId) {
        // 检查是否有关联关系
        checkSysRoleRelation(sysRoleId);
        SysRole sysRole = sysRoleMapper.selectById(sysRoleId);
        if (sysRole == null) {
            throw new BaseException("角色不存在");
        }
        if ("ROLE_ROOT".equals(sysRole.getName())) {
            throw new BaseException("不能删除超级管理员");
        }
        sysRole.setVisible(false);
        return sysRoleMapper.updateById(sysRole) == 1;
    }

    private void checkSysRoleRelation(Long sysRoleId) {
        checkMenuRoleRelation(null, sysRoleId);
        checkUserRoleRelation(null, sysRoleId);
        checkPermissionRoleRelation(null, sysRoleId);
    }

    // 检查菜单是否关联角色
    public void checkMenuRoleRelation(Long sysMenuId, Long sysRoleId) {
        if (sysMenuRolesMapper.hasMenuRoleRelation(sysMenuId, sysRoleId)) {
            throw new BaseException("菜单与角色有关联，不能删除");
        }
    }

    // 检查用户是否关联角色
    public void checkUserRoleRelation(Long sysUserId, Long sysRoleId) {
        if (sysUserRolesMapper.hasUserRoleRelation(sysUserId, sysRoleId)) {
            throw new BaseException("用户与角色有关联，不能删除");
        }
    }

    // 检查权限是否关联角色
    public void checkPermissionRoleRelation(Long sysPermissionId, Long sysRoleId) {
        if (sysPermissionRolesMapper.hasPermissionRoleRelation(sysPermissionId, sysRoleId)) {
            throw new BaseException("权限与角色有关联，不能删除");
        }
    }

    // 修改角色与菜单的关联关系
    public void updateRoleMenuRelation(long sysRoleId, List<Long> sysMenuIds) {
        if (existSysRole(sysRoleId)) {
            throw new BaseException("角色不存在");
        }
        List<Long> menuIds = sysMenuRolesMapper.getMenuIdsByRoleId(sysRoleId);
        // 先删除
        for (long menuId : menuIds) {
            if (!sysMenuIds.contains(menuId)) {
                // 这种写法不规范，SysMenuRoles没有id
                sysMenuRolesMapper.deleteMenuRoleRelation(new SysMenuRoles(sysRoleId, menuId));
            }
        }
        for (long sysMenuId : sysMenuIds) {
            if (!sysMenuRolesMapper.hasMenuRoleRelation(sysMenuId, sysRoleId)) {
                sysMenuRolesMapper.insert(new SysMenuRoles(sysRoleId, sysMenuId));
            }
        }
    }

    public void updateRolePermissionRelation(long sysRoleId, List<Long> sysPermissionIds) {
        if (existSysRole(sysRoleId)) {
            throw new BaseException("角色不存在");
        }
        List<Long> permissionIds = sysPermissionRolesMapper.getPermissionIdsByRoleId(sysRoleId);
        // 先删除
        for (long permissionId : permissionIds) {
            if (!sysPermissionIds.contains(permissionId)) {
                sysPermissionRolesMapper.deletePermissionRoleRelation(new SysPermissionRoles(sysRoleId, permissionId));
            }
        }
        for (long sysPermissionId : sysPermissionIds) {
            if (!sysPermissionRolesMapper.hasPermissionRoleRelation(sysPermissionId, sysRoleId)) {
                sysPermissionRolesMapper.insert(new SysPermissionRoles(sysRoleId, sysPermissionId));
            }
        }
    }

    // 角色是否存在
    private boolean existSysRole(long sysRoleId) {
        return sysRoleMapper.selectById(sysRoleId) == null;
    }

    // 删除一个用户的所有角色
    public void removeSysUserRoles(SysUser sysUser) {
        sysUserRolesMapper.deleteUserRoles(sysUser.getId());
    }

    // 为用户添加多个角色
    public void addSysUserRoles(SysUser sysUser, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            sysUserRolesMapper.insert(new SysUserRoles(roleId, sysUser.getId()));
        }
    }
}
