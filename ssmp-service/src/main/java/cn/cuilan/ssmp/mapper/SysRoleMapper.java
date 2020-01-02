package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统角色Mapper
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据系统用户id获取所有角色
     *
     * @param sysUserId 系统用户id
     */
    @Select("select sr.* from t_sys_role as sr " +
            " left join t_sys_user_roles as sur on sr.id = sur.roles_id " +
            " where sur.sys_user_id = #{sysUserId}")
    List<SysRole> getCurrentUserRoles(@Param("sysUserId") Long sysUserId);

    /**
     * 查询所有可用权限列表
     */
    @Select("SELECT * FROM t_sys_role WHERE visible = TRUE ORDER BY id ASC")
    List<SysRole> getAllSysRoles();

    /**
     * 当前角色名称是否存在
     *
     * @param name 角色名称
     */
    @Select("SELECT count(*) = 0 FROM t_sys_role WHERE `name` = #{roleName}")
    boolean exsitRoleName(@Param("roleName") String name);

}
