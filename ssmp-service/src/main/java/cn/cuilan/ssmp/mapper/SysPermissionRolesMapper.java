package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysPermissionRoles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统权限与系统角色Mapper
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Mapper
public interface SysPermissionRolesMapper extends BaseMapper<SysPermissionRoles> {

    /**
     * 权限与角色是否已存在关联关系
     *
     * @param sysPermissionId 系统权限id
     * @param sysRoleId       系统角色id
     */
    @Select({"<script>" +
            "SELECT count(*) > 0 FROM t_sys_permission_roles WHERE 1 = 1 " +
            "<if test='sysPermissionId!=null'> AND sys_permission_id = #{sysPermissionId} </if>" +
            "<if test='sysRoleId!=null'> AND roles_id = #{sysRoleId} </if>" +
            "</script>"})
    boolean hasPermissionRoleRelation(@Param("sysPermissionId") Long sysPermissionId, @Param("sysRoleId") Long sysRoleId);

    /**
     * 物理删除权限与角色的关联关系
     */
    @Delete("DELETE FROM t_sys_permission_roles WHERE roles_id = #{rolesId} AND sys_permission_id = #{sysPermissionId}")
    void deletePermissionRoleRelation(SysPermissionRoles sysPermissionRoles);

    /**
     * 根据角色id获取与该角色关联的所有权限id列表
     *
     * @param sysRoleId 系统角色id
     */
    @Select("select spr.sys_permission_id from t_sys_permission_roles as spr where spr.roles_id = #{sysRoleId}")
    List<Long> getPermissionIdsByRoleId(@Param("sysRoleId") long sysRoleId);

}
