package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.SysMenu;
import cn.cuilan.ssmp.entity.SysOperationLog;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.observer.AbstractDataCreateObserver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysMenuCreateObserver extends AbstractDataCreateObserver<SysMenu> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    public SysMenuCreateObserver(BaseMapper<SysMenu> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regCreateObserver(Register register) {
        register.afterCreate("添加系统菜单", this::addSysMenu);
    }

    private void addSysMenu(SysMenu sysMenu) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.MENU_ADD, OperationType.MENU_ADD.getDesc(),
                loginUser.getId(), sysMenu.getClass().getSimpleName(), sysMenu.getId()));
    }

}
