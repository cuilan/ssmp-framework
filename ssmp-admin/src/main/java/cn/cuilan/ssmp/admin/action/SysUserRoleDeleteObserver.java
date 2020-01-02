package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.mapper.SysUserMapper;
import cn.cuilan.ssmp.observer.AbstractDataDeleteObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysUserRoleDeleteObserver extends AbstractDataDeleteObserver<SysUserRoles> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    public SysUserRoleDeleteObserver(BaseMapper<SysUserRoles> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regDeleteObserver(Register register) {
        register.beforeDelete("删除用户的角色", this::deleteSysUserRoles);
    }

    private void deleteSysUserRoles(SysUserRoles sysUserRoles) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        SysUser sysUser = sysUserMapper.selectById(sysUserRoles.getSysUserId());
        SysRole sysRole = sysRoleMapper.selectById(sysUserRoles.getRolesId());
        String desc = String.format("管理员: [%s] 删除了用户: [%s] 与角色: [%s] 的关联关系。角色id: %d, 角色名称: %s",
                loginUser.getUsername(),
                sysUser.getUsername(),
                sysRole.getDescription(),
                sysRole.getId(),
                sysRole.getName());
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_USER_DELETE_RELATION,
                desc, loginUser.getId(), sysUserRoles.getClass().getSimpleName(), null));
    }
}
