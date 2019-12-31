package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 系统用户service
 *
 * @author zhang.yan
 * @date 2019/12/31
 */
@Service
public class SysUserService extends BaseService<SysUserMapper, SysUser> {

    @Resource
    private SysUserMapper sysUserMapper;

    public SysUser getSysUserInfo(Long sysUserId, String username) {
        SysUser sysUser;
        if (sysUserId == null || sysUserId == 0) {
            sysUser = sysUserMapper.getByUsername(username);
        } else {
            sysUser = sysUserMapper.selectById(sysUserId);
        }

        if (sysUser == null) {
            return sysUser;
        }
        // TODO
        // 获取并设置角色
        //sysUser.setRoles(sysRoleMapper.getCurrentUserRoles(sysUser.getId()));
        // 获取并设置当前用户的权限
        //sysUser.setPermissions(sysPermissionMapper.getCurrentUserPermissions(sysUser.getId()));
        // 获取并设置当前用户可见的菜单
        //List<SysMenu> currentUserMenus = sysMenuMapper.getCurrentUserMenus(sysUser.getId());
        //sysUser.setMenus(sysMenuService.menuToOrder(currentUserMenus));
        return sysUser;
    }
}
