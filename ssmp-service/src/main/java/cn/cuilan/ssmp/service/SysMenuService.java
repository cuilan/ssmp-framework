package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.entity.SysMenu;
import cn.cuilan.ssmp.entity.SysRole;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.mapper.SysMenuMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SysMenuService {

    private static final Long ROOT_PARENT_ID = 0L;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleService sysRoleService;

    public SysMenu getSysMenuById(long sysMenuId) {
        SysMenu sysMenu = sysMenuMapper.selectById(sysMenuId);
        sysMenu.setSubSysMenus(sysMenuMapper.getAllSysMenuByParentId(sysMenu.getId()));
        return sysMenu;
    }

    public List<SysMenu> getAllSysMenuByParentId(long parentId) {
        List<SysMenu> allMenu = sysMenuMapper.getAllSysMenuByParentId(parentId);
        return menuToOrder(allMenu);
    }

    public List<SysMenu> getSysMenuByRole(long sysRoleId, SysUser loginUser) {
        SysRole sysRole = sysRoleMapper.selectById(sysRoleId);
        if (sysRole == null) {
            throw new BaseException("角色不存在");
        }
        if (!loginUser.getRoles().contains(sysRole)) {
            throw new BaseException("无权限查看该角色");
        }
        List<SysMenu> allSysMenus = sysMenuMapper.getAllSysMenuByParentId(ROOT_PARENT_ID);
        List<SysMenu> currentRoleMenus = sysMenuMapper.getSysMenuByRoleId(sysRoleId);
        for (SysMenu sysMenu : allSysMenus) {
            if (currentRoleMenus.contains(sysMenu)) {
                sysMenu.setRetain(true);
            }
        }
        return menuToOrder(allSysMenus);
    }

    public boolean add(long parentId, SysMenu sysMenu) {
        sysMenu.setParentId(parentId);
        Integer maxProority = sysMenuMapper.getMaxPriorityByParentId(parentId);
        sysMenu.setPriority(maxProority == null ? 0 : maxProority + 1);
        sysMenu.setVisible(true);
        return sysMenuMapper.insert(sysMenu) == 1;
    }

    public boolean update(SysMenu sysMenu) {
        if (sysMenu.getId() == null || sysMenu.getId() == 0) {
            throw new BaseException("菜单id不能为空");
        }
        return sysMenuMapper.updateById(sysMenu) == 1;
    }

    public boolean removeSysMenu(long sysMenuId) {
        // 检查是否包含子菜单
        checkHasSubMenu(sysMenuId);
        // 检查菜单与角色关联，有关联不能删除
        sysRoleService.checkMenuRoleRelation(sysMenuId, null);
        SysMenu sysMenu = sysMenuMapper.selectById(sysMenuId);
        if (sysMenu == null) {
            throw new BaseException("菜单不存在");
        }
        sysMenu.setVisible(false);
        return sysMenuMapper.updateById(sysMenu) == 1;
    }

    // 检查是否包含子菜单
    private void checkHasSubMenu(long sysMenuId) {
        if (sysMenuMapper.hasSubMenu(sysMenuId)) {
            throw new BaseException("该菜单下包含子菜单，不能删除");
        }
    }


    public List<SysMenu> menuToOrder(List<SysMenu> menus) {
        if (menus != null && menus.size() > 0) {
            Map<Long, List<SysMenu>> menuMap = new HashMap<>();
            menus.forEach((menu) -> {
                if (menuMap.containsKey(menu.getParentId())) {
                    menuMap.get(menu.getParentId()).add(menu);
                } else {
                    List<SysMenu> subMenus = new ArrayList<>();
                    subMenus.add(menu);
                    menuMap.put(menu.getParentId(), subMenus);
                }
            });
            menuToOrderIter(menuMap, ROOT_PARENT_ID);
            menus = menuMap.get(ROOT_PARENT_ID);
            this.menuSort(menus);
        }
        return menus;
    }

    // 对指定菜单排序
    private void menuToOrderIter(Map<Long, List<SysMenu>> map, Long parentId) {
        if (map != null && map.size() > 1) {
            List<SysMenu> sysMenus = map.get(parentId);
            sysMenus.forEach(menu -> {
                // 如果当前菜单是父级
                if (map.containsKey(menu.getId())) {
                    menuToOrderIter(map, menu.getId());
                    menu.setSubSysMenus(map.get(menu.getId()));
                }
            });
        }
    }

    // 对菜单排序
    private void menuSort(List<SysMenu> sysMenus) {
        for (SysMenu sysMenu : sysMenus) {
            if (sysMenu.getSubSysMenus() != null && sysMenu.getSubSysMenus().size() > 0) {
                menuSort(sysMenu.getSubSysMenus());
            }
        }
        sysMenus.sort(Comparator.comparingInt(SysMenu::getPriority));
    }
}
