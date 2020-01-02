package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.mapper.SysUserMapper;
import cn.cuilan.ssmp.observer.AbstractDataCreateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysUserRoleCreateObserver extends AbstractDataCreateObserver<SysUserRoles> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    public SysUserRoleCreateObserver(BaseMapper<SysUserRoles> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regCreateObserver(Register register) {
        register.afterCreate("为用户关联角色", this::createSysUserRoles);
    }

    private void createSysUserRoles(SysUserRoles sysUserRoles) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        SysUser sysUser = sysUserMapper.selectById(sysUserRoles.getSysUserId());
        SysRole sysRole = sysRoleMapper.selectById(sysUserRoles.getRolesId());
        String desc = String.format("管理员: [%s] 为用户: [%s] 添加了角色: [%s]。角色id: %d, 角色名称: %s",
                loginUser.getUsername(),
                sysUser.getUsername(),
                sysRole.getDescription(),
                sysRole.getId(),
                sysRole.getName());
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_USER_RELATION,
                desc, loginUser.getId(), sysUserRoles.getClass().getSimpleName(), null));
    }
}
