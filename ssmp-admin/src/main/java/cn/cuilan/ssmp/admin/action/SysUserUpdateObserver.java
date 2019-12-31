package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.SysOperationLog;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.observer.AbstractDataUpdateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class SysUserUpdateObserver extends AbstractDataUpdateObserver<SysUser> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    public SysUserUpdateObserver(BaseMapper<SysUser> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regUpdateObserver(Register register) {
        register.afterUpdate("系统用户登录", this::sysUserLogin);
        register.afterUpdate("系统用户修改密码", this::sysUserResetPassword);
        register.afterUpdate("启用或禁用系统用户", this::activeOrInvisibleSysUser);
        // 修改日志太多，暂时不记录
        register.afterUpdate("修改系统用户其他属性", this::updateSysUser);
    }

    private void sysUserLogin(SysUser old, SysUser updated) {
        if (Objects.equals(old.getLastLoginTime(), updated.getLastLoginTime())) {
            return;
        }
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        SysOperationLog sysOperationLog = new SysOperationLog(OperationType.USER_LOGIN, OperationType.USER_LOGIN.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId());
        sysOperationLogMapper.insert(sysOperationLog);
    }

    private void sysUserResetPassword(SysUser old, SysUser updated) {
        if (Objects.equals(old.getPassword(), updated.getPassword())) {
            return;
        }
        SysOperationLog sysOperationLog = new SysOperationLog(OperationType.USER_RESET_PASSWORD, OperationType.USER_RESET_PASSWORD.getDesc(),
                updated.getId(), updated.getClass().getSimpleName(), updated.getId());
        sysOperationLogMapper.insert(sysOperationLog);
    }

    private void activeOrInvisibleSysUser(SysUser old, SysUser updated) {
        if (Objects.equals(old.getStatus(), updated.getStatus())) {
            return;
        }
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        if (old.getStatus() == 1 && updated.getStatus() == 0) {
            SysOperationLog sysOperationLog = new SysOperationLog(OperationType.USER_INVISIBLE, OperationType.USER_INVISIBLE.getDesc(),
                    loginUser.getId(), updated.getClass().getSimpleName(), updated.getId());
            sysOperationLogMapper.insert(sysOperationLog);
        }
        if (old.getStatus() == 0 && updated.getStatus() == 1) {
            SysOperationLog sysOperationLog = new SysOperationLog(OperationType.USER_ACTIVATE, OperationType.USER_ACTIVATE.getDesc(),
                    loginUser.getId(), updated.getClass().getSimpleName(), updated.getId());
            sysOperationLogMapper.insert(sysOperationLog);
        }
    }

    private void updateSysUser(SysUser old, SysUser updated) {
        // 登录IP或登录时间被修改，不记录修改操作日志
        if (!Objects.equals(old.getLastLoginIp(), updated.getLastLoginIp())
                || !Objects.equals(old.getLastLoginTime(), updated.getLastLoginTime())) {
            return;
        }
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        SysOperationLog sysOperationLog = new SysOperationLog(OperationType.USER_UPDATE, OperationType.USER_UPDATE.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId());
        sysOperationLogMapper.insert(sysOperationLog);
    }

}
