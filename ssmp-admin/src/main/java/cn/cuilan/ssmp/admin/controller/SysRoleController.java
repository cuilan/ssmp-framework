package cn.cuilan.ssmp.admin.controller;

import cn.cuilan.ssmp.entity.SysRole;
import cn.cuilan.ssmp.service.SysRoleService;
import cn.cuilan.ssmp.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @GetMapping("/api/admin/sysrole/query")
    @PreAuthorize("authenticated and hasPermission('role', 'query')")
    public Result getSysRoles(@RequestParam(value = "rid", required = false) Long sysRoleId) {
        return Result.success(sysRoleService.getSysRoles(sysRoleId));
    }

    @PostMapping("/api/admin/sysrole/add")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('role', 'add')")
    public Result addSysRoles(SysRole sysRole) {
        if (sysRoleService.addSysRole(sysRole)) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败");
    }

    @PostMapping("/api/admin/sysrole/update")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('role', 'update')")
    public Result updateSysRoles(SysRole sysRole) {
        if (sysRoleService.update(sysRole)) {
            return Result.success("修改成功");
        }
        return Result.fail("修改失败");
    }

    @PostMapping("/api/admin/sysrole/remove")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('role', 'delete')")
    public Result removeSysRoles(@RequestParam("rid") long sysRoleId) {
        if (sysRoleService.removeSysRole(sysRoleId)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    // 修改角色与菜单的关联关系
    @PostMapping("/api/admin/sysrole/role-menu-relation")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('role', 'update')")
    public Result updateRoleMenuRelation(@RequestParam("rid") long sysRoleId,
                                         @RequestParam("mid[]") long[] sysMenuIds) {
        List<Long> menuIds = new ArrayList<>(sysMenuIds.length);
        for (long menuId : sysMenuIds) {
            menuIds.add(menuId);
        }
        sysRoleService.updateRoleMenuRelation(sysRoleId, menuIds);
        return Result.success();
    }

    // 修改角色与权限的关联关系
    @PostMapping("/api/admin/sysrole/role-permission-relation")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('role', 'update')")
    public Result updateRolePermissionRelation(@RequestParam("rid") long sysRoleId,
                                               @RequestParam("pid[]") long[] sysPermissionIds) {
        List<Long> permissionIds = new ArrayList<>(sysPermissionIds.length);
        for (long permissionId : sysPermissionIds) {
            permissionIds.add(permissionId);
        }
        sysRoleService.updateRolePermissionRelation(sysRoleId, permissionIds);
        return Result.success();
    }
}
