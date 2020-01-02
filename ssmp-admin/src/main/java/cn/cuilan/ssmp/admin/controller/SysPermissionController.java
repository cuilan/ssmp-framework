package cn.cuilan.ssmp.admin.controller;

import cn.cuilan.ssmp.admin.annotation.Logined;
import cn.cuilan.ssmp.entity.SysPermission;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.service.SysPermissionService;
import cn.cuilan.ssmp.utils.result.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SysPermissionController {

    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 查看自己的可见权限
     */
    @GetMapping("/api/admin/syspermission/mypermissions")
    @PreAuthorize("authenticated")
    public Result getSysPermissionByLoginUser(@Logined SysUser sysUser) {
        return Result.success(sysUser.getPermissions());
    }

    /**
     * 根据角色查看权限，需要有权限查询权限
     */
    @GetMapping("/api/admin/syspermission/role-permissions")
    @PreAuthorize("authenticated and hasPermission('permission', 'query')")
    public Result getSysPermissionByRole(@RequestParam("rid") long sysRoleId,
                                         @Logined SysUser sysUser) {
        return Result.success(sysPermissionService.getSysPermissionByRole(sysRoleId, sysUser));
    }

    @GetMapping("/api/admin/syspermission/query")
    @PreAuthorize("authenticated and hasPermission('permission', 'query')")
    public Result getAllPermissions(@RequestParam(value = "pid", required = false) Long sysPermissionId) {
        return Result.success(sysPermissionService.getSysPermissions(sysPermissionId));
    }

    @PostMapping("/api/admin/syspermission/add")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('permission', 'add')")
    public Result addPermission(SysPermission sysPermission) {
        if (sysPermissionService.addSysPermission(sysPermission)) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败");
    }

    @PostMapping("/api/admin/syspermission/update")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('permission', 'update')")
    public Result updatePermission(SysPermission sysPermission) {
        if (sysPermissionService.update(sysPermission)) {
            return Result.success("修改成功");
        }
        return Result.fail("修改失败");
    }

    @PostMapping("/api/admin/syspermission/remove")
    @PreAuthorize("hasAnyRole('ROLE_ROOT') and hasPermission('permission', 'delete')")
    public Result removePermission(@RequestParam("pid") long sysPermissionId) {
        if (sysPermissionService.removeSysPermission(sysPermissionId)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }
}
