package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.mapper.SysPermissionMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.observer.AbstractDataDeleteObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysPermissionRoleDeleteObserver extends AbstractDataDeleteObserver<SysPermissionRoles> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    public SysPermissionRoleDeleteObserver(BaseMapper<SysPermissionRoles> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regDeleteObserver(Register register) {
        register.beforeDelete("删除权限与角色的关联", this::deleteSysPermissionRoles);
    }

    private void deleteSysPermissionRoles(SysPermissionRoles sysPermissionRoles) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        SysPermission sysPermission = sysPermissionMapper.selectById(sysPermissionRoles.getSysPermissionId());
        SysRole sysRole = sysRoleMapper.selectById(sysPermissionRoles.getRolesId());
        String desc = String.format("管理员: [%s] 删除了权限: [%s] 与角色: [%s] 的关联关系。角色id: %d, 角色名称: %s",
                loginUser.getUsername(),
                sysPermission.getDescription(),
                sysRole.getDescription(),
                sysRole.getId(),
                sysRole.getName());
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_PERMISSION_DELETE_RELATION,
                desc, loginUser.getId(), sysPermissionRoles.getClass().getSimpleName(), null));
    }
}
