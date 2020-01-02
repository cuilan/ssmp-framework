package cn.cuilan.ssmp.admin.controller;

import cn.cuilan.ssmp.admin.annotation.Logined;
import cn.cuilan.ssmp.entity.SysMenu;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.service.SysMenuService;
import cn.cuilan.ssmp.utils.result.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;

@RestController
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 查看自己的可见菜单，需要登录
     */
    @GetMapping("/api/admin/sysmenu/mymenus")
    @PreAuthorize("authenticated")
    public Result getSysMenuByLoginUser(@Logined SysUser sysUser) {
        return Result.success(sysUser.getMenus());
    }

    /**
     * 根据角色查看菜单，需要有菜单查询权限
     */
    @GetMapping("/api/admin/sysmenu/role-menus")
    @PreAuthorize("authenticated and hasPermission('menu', 'query')")
    public Result getSysMenuByRole(@RequestParam("rid") long sysRoleId,
                                   @Logined SysUser sysUser) {
        return Result.success(sysMenuService.getSysMenuByRole(sysRoleId, sysUser));
    }

    /**
     * 获取所有系统菜单，默认查询父id为0的菜单，即根菜单。
     * 需要用户具有以下权限：
     * 1.必须登录
     * 2.必须有 menu_query 权限
     */
    @GetMapping("/api/admin/sysmenu/query")
    @PreAuthorize("authenticated and hasPermission('menu', 'query')")
    public Result getAllSysMenu(@RequestParam(value = "parentId", defaultValue = "0") long parentId,
                                @RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            return Result.success(Collections.singletonList(sysMenuService.getSysMenuById(id)));
        }
        return Result.success(sysMenuService.getAllSysMenuByParentId(parentId));
    }

    /**
     * 添加系统菜单，权限认证条件为：
     * 1.必须登录 (有条件2存在，条件1可以忽略)
     * 2.仅 超级管理员(ROLE_ROOT)，管理员(ROLE_ADMIN)有权限操作
     * 3.必须有 menu_add 权限
     * 需同时满足以上三个条件
     */
    @PostMapping("/api/admin/sysmenu/add")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('menu', 'add')")
    public Result addSysMenu(@RequestParam(value = "parentId", defaultValue = "0") long parentId,
                             SysMenu sysMenu) {
        if (sysMenuService.add(parentId, sysMenu)) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败");
    }

    @PostMapping("/api/admin/sysmenu/update")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('menu', 'update')")
    public Result updateSysMenu(SysMenu sysMenu) {
        if (sysMenuService.update(sysMenu)) {
            return Result.success("修改成功");
        }
        return Result.fail("修改失败");
    }

    @PostMapping("/api/admin/sysmenu/remove")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('menu', 'delete')")
    public Result removeSysMenu(@RequestParam("id") long sysMenuId) {
        if (sysMenuService.removeSysMenu(sysMenuId)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }
}
