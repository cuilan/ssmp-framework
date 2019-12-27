package cn.cuilan.mapper;

import cn.cuilan.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User getByUsername(@Param("username") String username);

    @Select("SELECT * FROM t_user WHERE real_name = #{realName}")
    User getByRealName(@Param("realName") String realName);

    /**
     * 获取全部用户，仅供参数使用，慎用
     *
     * @return 返回当前数据库表中所有的数据
     */
    @Select("SELECT * FROM t_user")
    Page<User> getAllUser(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

}
