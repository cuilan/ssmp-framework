package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysMenuRoles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统菜单与系统角色关联关系Mapper
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Mapper
public interface SysMenuRolesMapper extends BaseMapper<SysMenuRoles> {

    /**
     * 菜单与角色是否存在关联关系
     *
     * @param sysMenuId 系统菜单id
     * @param sysRoleId 系统角色id
     */
    @Select({"<script>" +
            "SELECT count(*) > 0 FROM t_sys_menu_roles WHERE 1 = 1 " +
            "<if test='sysMenuId!=null'> AND sys_menu_id = #{sysMenuId} </if>" +
            "<if test='sysRoleId!=null'> AND roles_id = #{sysRoleId} </if>" +
            "</script>"})
    boolean hasMenuRoleRelation(@Param("sysMenuId") Long sysMenuId, @Param("sysRoleId") Long sysRoleId);

    /**
     * 根据角色id查询与该角色关联的所有菜单id
     *
     * @param sysRoleId 角色id
     */
    @Select("select smr.sysMenuId from t_sys_menu_roles as smr where smr.roles_id = #{sysRoleId}")
    List<Long> getMenuIdsByRoleId(@Param("sysRoleId") Long sysRoleId);

    /**
     * 物理删除菜单与角色的关联关系
     */
    @Delete("DELETE FROM t_sys_menu_roles WHERE roles_id = #{rolesId} AND sys_menu_id = #{sysMenuId}")
    void deleteMenuRoleRelation(SysMenuRoles sysMenuRoles);
}
