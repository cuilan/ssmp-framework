package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author zhang.yan
 * @date 2019/12/31
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("select * from t_sys_user where username = #{username}")
    SysUser getByUsername(@Param("username") String username);

}
