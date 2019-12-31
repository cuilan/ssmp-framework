package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.SysOperationLog;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.observer.DataCreateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysUserCreateObserver extends DataCreateObserver<SysUser> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    public SysUserCreateObserver(BaseMapper<SysUser> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regCreateObserver(Register register) {
        register.afterCreate("添加系统用户", this::addSysUser);
    }

    private void addSysUser(SysUser sysUser) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.USER_ADD, OperationType.USER_ADD.getDesc(),
                loginUser.getId(), sysUser.getClass().getSimpleName(), sysUser.getId()));
    }

}
