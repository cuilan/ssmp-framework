package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.observer.AbstractDataCreateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysRoleCreateObserver extends AbstractDataCreateObserver<SysRole> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    public SysRoleCreateObserver(BaseMapper<SysRole> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regCreateObserver(Register register) {
        register.afterCreate("添加系统角色", this::addSysRole);
    }

    private void addSysRole(SysRole sysRole) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_ADD, OperationType.ROLE_ADD.getDesc(),
                loginUser.getId(), sysRole.getClass().getSimpleName(), sysRole.getId()));
    }
}
