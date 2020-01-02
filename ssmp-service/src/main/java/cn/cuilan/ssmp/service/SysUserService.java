package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.entity.SysMenu;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.mapper.SysMenuMapper;
import cn.cuilan.ssmp.mapper.SysPermissionMapper;
import cn.cuilan.ssmp.mapper.SysRoleMapper;
import cn.cuilan.ssmp.mapper.SysUserMapper;
import cn.cuilan.ssmp.redis.RedisUtils;
import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysRoleService sysRoleService;

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
        // 获取并设置角色
        sysUser.setRoles(sysRoleMapper.getCurrentUserRoles(sysUser.getId()));
        // 获取并设置当前用户的权限
        sysUser.setPermissions(sysPermissionMapper.getCurrentUserPermissions(sysUser.getId()));
        // 获取并设置当前用户可见的菜单
        List<SysMenu> currentUserMenus = sysMenuMapper.getCurrentUserMenus(sysUser.getId());
        sysUser.setMenus(sysMenuService.menuToOrder(currentUserMenus));
        return sysUser;
    }

    public SysUser addSysUser(SysUser sysUser, List<Long> roles) {
        SysUser exitPhone = sysUserMapper.getByPhone(sysUser.getPhone());
        if (exitPhone != null) {
            throw new BaseException("手机号重复");
        }
        SysUser exitUsername = sysUserMapper.getByUsername(sysUser.getUsername());
        if (exitUsername != null) {
            throw new BaseException("账户名重复");
        }
        sysUser.setAdmin(false);

        sysUserMapper.insert(sysUser);
        sysRoleService.addSysUserRoles(sysUser, roles);

        return sysUser;
    }

    public String genTmpPwd() {
        return RandomUtil.randomInt(100000, 999999) + "";
    }

    public void updateSysUser(SysUser sysUser) {
        sysUserMapper.updateById(sysUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSysUser(SysUser sysUser, List<Long> roles) {
        updateSysUser(sysUser);
        sysRoleService.removeSysUserRoles(sysUser);
        sysRoleService.addSysUserRoles(sysUser, roles);
    }

    public Page<SysUser> getSysUser(Long userId, String username, Integer status, int pageNum, int pageSize) {
        if (userId != null && userId > 0) {
            Page<SysUser> sysUsers = new Page<>();
            SysUser sysUser = sysUserMapper.selectById(userId);
            if (sysUser != null) {
                sysUser.setRoles(sysRoleMapper.getCurrentUserRoles(sysUser.getId()));
                sysUsers.add(sysUser);
            }
            return sysUsers;
        }
        return sysUserMapper.getAllSysUsers(username, status, pageNum, pageSize);
    }
}
