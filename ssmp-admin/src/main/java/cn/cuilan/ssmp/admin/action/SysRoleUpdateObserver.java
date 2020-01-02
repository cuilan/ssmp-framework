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
public class SysRoleUpdateObserver extends AbstractDataUpdateObserver<SysRole> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    public SysRoleUpdateObserver(BaseMapper<SysRole> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regUpdateObserver(Register register) {
        register.afterUpdate("修改系统角色", this::updateSysRole);
        register.afterUpdate("删除系统角色", this::removeSysRole);
    }

    private void updateSysRole(SysRole old, SysRole updated) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_UPDATE, OperationType.ROLE_UPDATE.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId()));
    }

    private void removeSysRole(SysRole old, SysRole updated) {
        if (Objects.equals(old.getVisible(), updated.getVisible())) {
            return;
        }
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.ROLE_DELETE, OperationType.ROLE_DELETE.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId()));
    }
}
