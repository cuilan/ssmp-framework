package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.entity.SysPermission;
import cn.cuilan.ssmp.entity.SysRole;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.mapper.SysPermissionMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.utils.RoleAndPermissionNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class SysPermissionService {

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleService sysRoleService;

    public List<SysPermission> getSysPermissions(Long sysPermissionId) {
        if (sysPermissionId != null && sysPermissionId > 0) {
            return Collections.singletonList(sysPermissionMapper.selectById(sysPermissionId));
        }
        return sysPermissionMapper.getAllSysPermissions();
    }

    public List<SysPermission> getSysPermissionByRole(long sysRoleId, SysUser loginUser) {
        SysRole sysRole = sysRoleMapper.selectById(sysRoleId);
        if (sysRole == null) {
            throw new BaseException("角色不存在");
        }
        if (!loginUser.getRoles().contains(sysRole)) {
            throw new BaseException("无权限查看该角色");
        }
        List<SysPermission> allSysPermissions = sysPermissionMapper.getAllSysPermissions();
        List<SysPermission> currentRolePermissions = sysPermissionMapper.getSysPermissionByRole(sysRoleId);
        for (SysPermission sysPermission : allSysPermissions) {
            if (currentRolePermissions.contains(sysPermission)) {
                sysPermission.setRetain(true);
            }
        }
        return allSysPermissions;
    }

    public boolean addSysPermission(SysPermission sysPermission) {
        if (StringUtils.isBlank(sysPermission.getName())) {
            throw new BaseException("权限名称必填");
        }
        RoleAndPermissionNameUtils.checkName(sysPermission.getName());
        if (!sysPermissionMapper.exsitPermissionName(sysPermission.getName())) {
            throw new BaseException("该权限已存在");
        }
        sysPermission.setVisible(true);
        return sysPermissionMapper.insert(sysPermission) == 1;
    }

    public boolean update(SysPermission sysPermission) {
        if (sysPermission.getId() == null || sysPermission.getId() == 0) {
            throw new BaseException("权限id不能为空");
        }
        RoleAndPermissionNameUtils.checkName(sysPermission.getName());
        SysPermission dbSysPermission = sysPermissionMapper.selectById(sysPermission.getId());
        // 当权限名称时，校验权限名称是否已存在
        if (dbSysPermission != null && !dbSysPermission.getName().equals(sysPermission.getName())) {
            if (!sysPermissionMapper.exsitPermissionName(sysPermission.getName())) {
                throw new BaseException("该权限已存在");
            }
        }
        return sysPermissionMapper.updateById(sysPermission) == 1;
    }

    public boolean removeSysPermission(long sysPermissionId) {
        // 检查是否有关联关系
        sysRoleService.checkPermissionRoleRelation(sysPermissionId, null);
        SysPermission sysPermission = sysPermissionMapper.selectById(sysPermissionId);
        if (sysPermission == null) {
            throw new BaseException("权限不存在");
        }
        sysPermission.setVisible(false);
        return sysPermissionMapper.updateById(sysPermission) == 1;
    }
}
