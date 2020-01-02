package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.mapper.SysPermissionMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.observer.AbstractDataCreateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysPermissionRoleCreateObserver extends AbstractDataCreateObserver<SysPermissionRoles> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    public SysPermissionRoleCreateObserver(BaseMapper<SysPermissionRoles> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regCreateObserver(Register register) {
        register.afterCreate("为权限关联角色", this::createSysPermissionRoles);
    }

    private void createSysPermissionRoles(SysPermissionRoles sysPermissionRoles) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        SysPermission sysPermission = sysPermissionMapper.selectById(sysPermissionRoles.getSysPermissionId());
        SysRole sysRole = sysRoleMapper.selectById(sysPermissionRoles.getRolesId());
        String desc = String.format("管理员: [%s] 为权限: [%s] 添加了角色: [%s]。角色id: %d, 角色名称: %s",
                loginUser.getUsername(),
                sysPermission.getDescription(),
                sysRole.getDescription(),
                sysRole.getId(),
                sysRole.getName());
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_PERMISSION_RELATION,
                desc, loginUser.getId(), sysPermissionRoles.getClass().getSimpleName(), null));
    }
}
