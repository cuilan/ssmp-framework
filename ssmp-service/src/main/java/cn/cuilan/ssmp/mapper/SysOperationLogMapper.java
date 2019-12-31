package cn.cuilan.ssmp.mapper;

import cn.cuilan.ssmp.entity.SysOperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    @Select({"<script>" +
            "SELECT * FROM t_sys_operation_log WHERE 1 = 1 " +
            "<if test='sysUserId!=null'> AND sysUserId = #{sysUserId} </if>" +
            "<if test='startTime!=null'> AND createTime &gt;= #{startTime} </if>" +
            "<if test='endTime!=null'> AND createTime &lt; #{endTime} </if>" +
            " order by id desc" +
            "</script>"})
    Page<SysOperationLog> getOperationLogsByAdmin(@Param("sysUserId") Long sysUserId,
                                                  @Param("startTime") Long startTime,
                                                  @Param("endTime") Long endTime,
                                                  @Param("pageNum") int pageNum,
                                                  @Param("pageSize") int pageSize);

}
