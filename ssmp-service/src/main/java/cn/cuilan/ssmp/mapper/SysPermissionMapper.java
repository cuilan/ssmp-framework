package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统权限Mapper
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据系统用户id获取所有权限
     *
     * @param sysUserId 系统用户id
     */
    @Select({"SELECT sp.* FROM t_sys_permission AS sp " +
            " LEFT JOIN t_sys_permission_roles AS spr ON spr.sys_permission_id = sp.id " +
            " LEFT JOIN t_sys_role AS sr ON sr.id = spr.roles_id " +
            " LEFT JOIN t_sys_user_roles AS sur ON sr.id = sur.roles_id " +
            " WHERE sur.sys_user_id = #{sysUserId}"})
    List<SysPermission> getCurrentUserPermissions(@Param("sysUserId") Long sysUserId);

    /**
     * 获取所有可用权限
     */
    @Select("SELECT * FROM t_sys_permission WHERE visible = TRUE ORDER BY id ASC")
    List<SysPermission> getAllSysPermissions();

    /**
     * 当前权限名称是否存在
     *
     * @param name 权限名称
     */
    @Select("SELECT count(*) = 0 FROM t_sys_permission WHERE `name` = #{permissionName}")
    boolean exsitPermissionName(@Param("permissionName") String name);

    /**
     * 根据系统角色id查询与该角色关联的所有权限
     *
     * @param sysRoleId 系统角色id
     */
    @Select("SELECT sp.* FROM t_sys_permission AS sp " +
            " LEFT JOIN t_sys_permission_roles AS spr ON sp.id = spr.sys_permission_id " +
            " WHERE spr.roles_id = #{sysRoleId}")
    List<SysPermission> getSysPermissionByRole(@Param("sysRoleId") long sysRoleId);


}
