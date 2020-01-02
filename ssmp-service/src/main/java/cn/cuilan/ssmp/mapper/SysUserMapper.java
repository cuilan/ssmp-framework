package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统用户Mapper
 *
 * @author zhang.yan
 * @date 2019/12/31
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     */
    @Select("select * from t_sys_user where username = #{username}")
    SysUser getByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     */
    @Select("select * from t_sys_user where phone = #{phone}")
    SysUser getByPhone(@Param("phone") String phone);

    /**
     * 根据状态查询所有用户
     *
     * @param username 用户名，支持模糊查询
     * @param status   状态，启用或禁用
     * @param pageNum  页码
     * @param pageSize 分页大小
     */
    @Select({"<script>" +
            "SELECT * FROM t_sys_user WHERE 1 = 1 " +
            "<if test='username!=null'> AND username like CONCAT('%', #{username,jdbcType=VARCHAR},'%') </if>" +
            "<if test='status!=null'> AND status = #{status} </if>" +
            "</script>"})
    Page<SysUser> getAllSysUsers(
            @Param("username") String username,
            @Param("status") Integer status,
            @Param("pageNum") int pageNum,
            @Param("pageSize") int pageSize);

}
