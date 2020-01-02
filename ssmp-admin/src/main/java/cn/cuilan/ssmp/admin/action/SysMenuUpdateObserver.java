package cn.cuilan.ssmp.admin.action;

import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import cn.cuilan.ssmp.entity.SysMenu;
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
public class SysMenuUpdateObserver extends AbstractDataUpdateObserver<SysMenu> {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private CurrentLoginUserGetter currentLoginUserGetter;

    public SysMenuUpdateObserver(BaseMapper<SysMenu> baseMapper) {
        super(baseMapper);
    }

    @Override
    protected void regUpdateObserver(Register register) {
        register.afterUpdate("修改系统菜单", this::updateSysMenu);
        register.afterUpdate("删除系统菜单", this::removeSysMenu);
        register.afterUpdate("修改系统菜单排序", this::updateMenuSort);
        register.afterUpdate("修改系统菜单父子级关系", this::updateMenuRelation);
    }

    private void updateSysMenu(SysMenu old, SysMenu updated) {
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.MENU_UPDATE, OperationType.MENU_UPDATE.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId()));
    }

    private void removeSysMenu(SysMenu old, SysMenu updated) {
        if (Objects.equals(old.getVisible(), updated.getVisible())) {
            return;
        }
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.MENU_DELETE, OperationType.MENU_DELETE.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId()));
    }

    private void updateMenuSort(SysMenu old, SysMenu updated) {
        if (Objects.equals(old.getPriority(), updated.getPriority())) {
            return;
        }
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.MENU_SORT, OperationType.MENU_SORT.getDesc(),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId()));
    }

    private void updateMenuRelation(SysMenu old, SysMenu updated) {
        if (Objects.equals(old.getPriority(), updated.getPriority())) {
            return;
        }
        SysUser loginUser = currentLoginUserGetter.getCurrentLoginUser();
        sysOperationLogMapper.insert(new SysOperationLog(OperationType.MENU_RELATION,
                // 记录修改前后的父id到详细信息
                String.format("修改系统菜单父子级关系，原父id[%s] -> 修改后父id[%s]", old.getParentId(), updated.getParentId()),
                loginUser.getId(), updated.getClass().getSimpleName(), updated.getId()));
    }
}
