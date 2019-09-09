package cn.cuilan.bmp.mapper;

import cn.cuilan.bmp.entity.User;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取全部用户，仅供参数使用，慎用
     *
     * @return 返回当前数据库表中所有的数据
     */
    @Deprecated
    List<User> getAllUser();

}
