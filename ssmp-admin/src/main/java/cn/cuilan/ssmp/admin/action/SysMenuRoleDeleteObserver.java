package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysMenuMapper;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.observer.AbstractDataDeleteObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysMenuRoleDeleteObserver extends AbstractDataDeleteObserver<SysMenuRoles> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    public SysMenuRoleDeleteObserver(BaseMapper<SysMenuRoles> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regDeleteObserver(Register register) {
        register.beforeDelete("删除菜单与角色的关联", this::deleteSysMenuRoles);
    }

    private void deleteSysMenuRoles(SysMenuRoles sysMenuRoles) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        SysMenu sysMenu = sysMenuMapper.selectById(sysMenuRoles.getSysMenuId());
        SysRole sysRole = sysRoleMapper.selectById(sysMenuRoles.getRolesId());
        String desc = String.format("管理员: [%s] 删除了菜单: [%s] 与角色: [%s] 的关联关系。角色id: %d, 角色名称: %s",
                loginUser.getUsername(),
                sysMenu.getName(),
                sysRole.getDescription(),
                sysRole.getId(),
                sysRole.getName());
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_MENU_DELETE_RELATION,
                desc, loginUser.getId(), sysMenuRoles.getClass().getSimpleName(), null));
    }
}
