package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统菜单Mapper
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据父id查询当前父菜单下最大排序值
     *
     * @param parentId 父菜单id
     */
    @Select("select max(priority) from t_sys_menu where parent_id = #{parentId}")
    Integer getMaxPriorityByParentId(@Param("parentId") long parentId);

    /**
     * 根据父id查询该父菜单下所有子菜单
     *
     * @param parentId 父菜单id
     */
    @Select("<script>" +
            "select * from t_sys_menu where 1 = 1 " +
            "<if test='parentId!=0'> and parent_id = #{parentId} </if>" +
            " and visible = true" +
            "</script>")
    List<SysMenu> getAllSysMenuByParentId(@Param("parentId") long parentId);

    /**
     * 根据用户查询与该用户关联的系统菜单
     *
     * @param sysUserId 系统用户id
     */
    @Select({"SELECT sm.* FROM t_sys_menu AS sm " +
            " LEFT JOIN t_sys_menu_roles AS smr ON smr.sys_menu_id = sm.id " +
            " LEFT JOIN t_sys_role AS sr ON sr.id = smr.roles_id " +
            " LEFT JOIN t_sys_user_roles AS sur ON sr.id = sur.roles_id " +
            " WHERE sur.sys_user_id = #{sysUserId}"})
    List<SysMenu> getCurrentUserMenus(@Param("sysUserId") Long sysUserId);

    /**
     * 是否有子菜单，如果有，不可删除
     *
     * @param sysMenuId 菜单id
     */
    @Select("SELECT count(*) > 0 FROM t_sys_menu WHERE parent_id = #{parentId}")
    boolean hasSubMenu(@Param("parentId") long sysMenuId);

    /**
     * 根据角色id查询与该角色关联的所有菜单
     *
     * @param sysRoleId 角色id
     */
    @Select("SELECT sm.* FROM t_sys_menu AS sm " +
            " LEFT JOIN t_sys_menu_roles AS smr ON sm.id = smr.sys_menu_id " +
            " WHERE smr.roles_id = #{sysRoleId}")
    List<SysMenu> getSysMenuByRoleId(@Param("sysRoleId") long sysRoleId);

}
