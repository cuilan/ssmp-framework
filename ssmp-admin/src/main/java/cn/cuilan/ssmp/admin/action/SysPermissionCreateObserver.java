package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.SysOperationLog;
import cn.cuilan.ssmp.entity.SysPermission;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.observer.AbstractDataCreateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysPermissionCreateObserver extends AbstractDataCreateObserver<SysPermission> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    public SysPermissionCreateObserver(BaseMapper<SysPermission> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regCreateObserver(Register register) {
        register.afterCreate("添加系统权限", this::addSysPermission);
    }

    private void addSysPermission(SysPermission sysPermission) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.PERMISSION_ADD, OperationType.PERMISSION_ADD.getDesc(),
                loginUser.getId(), sysPermission.getClass().getSimpleName(), sysPermission.getId()));
    }
}
