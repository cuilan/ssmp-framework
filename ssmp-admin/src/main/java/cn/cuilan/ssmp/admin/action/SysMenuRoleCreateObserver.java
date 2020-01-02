package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysMenuMapper;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.observer.AbstractDataCreateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysMenuRoleCreateObserver extends AbstractDataCreateObserver<SysMenuRoles> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    public SysMenuRoleCreateObserver(BaseMapper<SysMenuRoles> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regCreateObserver(Register register) {
        register.afterCreate("为菜单关联角色", this::createSysMenuRoles);
    }

    private void createSysMenuRoles(SysMenuRoles sysMenuRoles) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        SysMenu sysMenu = sysMenuMapper.selectById(sysMenuRoles.getSysMenuId());
        SysRole sysRole = sysRoleMapper.selectById(sysMenuRoles.getRolesId());
        String desc = String.format("管理员: [%s] 为菜单: [%s] 添加了角色: [%s]。角色id: %d, 角色名称: %s",
                loginUser.getUsername(),
                sysMenu.getName(),
                sysRole.getDescription(),
                sysRole.getId(),
                sysRole.getName());
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_MENU_RELATION,
                desc, loginUser.getId(), sysMenuRoles.getClass().getSimpleName(), null));
    }
}
