package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysUserRoles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统用户与系统角色关联关系Mapper
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Mapper
public interface SysUserRolesMapper extends BaseMapper<SysUserRoles> {

    /**
     * 用户与角色是否已存在关联关系
     *
     * @param sysUserId 系统用户id
     * @param sysRoleId 系统角色id
     */
    @Select({"<script>" +
            "SELECT count(*) > 0 FROM t_sys_user_roles WHERE 1 = 1 " +
            "<if test='sysUserId!=null'> AND sys_user_id = #{sysUserId} </if>" +
            "<if test='sysRoleId!=null'> AND roles_id = #{sysRoleId} </if>" +
            "</script>"})
    boolean hasUserRoleRelation(@Param("sysUserId") Long sysUserId, @Param("sysRoleId") Long sysRoleId);

    /**
     * 通过系统用户ID删除用户的所有角色
     *
     * @param sysUserId 系统用户ID
     */
    @Select("DELETE from t_sys_user_roles WHERE sys_user_id = #{sysUserId}")
    void deleteUserRoles(@Param("sysUserId") Long sysUserId);

}
