package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.*;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.observer.AbstractDataUpdateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class SysPermissionUpdateObserver extends AbstractDataUpdateObserver<SysPermission> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    public SysPermissionUpdateObserver(BaseMapper<SysPermission> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regUpdateObserver(Register register) {
        register.afterUpdate("修改系统权限", this::updateSysPermission);
        register.afterUpdate("删除系统权限", this::removeSysPermission);
    }

    private void updateSysPermission(SysPermission old, SysPermission updated) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.PERMISSION_UPDATE,
                OperationType.PERMISSION_UPDATE.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId()));
    }

    private void removeSysPermission(SysPermission old, SysPermission updated) {
        if (Objects.equals(old.getVisible(), updated.getVisible())) {
            return;
        }
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.PERMISSION_DELETE,
                OperationType.PERMISSION_DELETE.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId()));
    }
}
