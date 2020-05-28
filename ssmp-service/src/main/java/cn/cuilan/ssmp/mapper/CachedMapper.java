package cn.cuilan.ssmp.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 基础缓存 Mapper 映射，提供 MyBatisPlus 缓存能力的增强方法
 *
 * @author zhang.yan
 * @date 2020/5/27
 */
@Mapper
public interface CachedMapper<T> extends CommonMapper<T> {

    /**
     * 根据id查询缓存，如果缓存中没有，则查询数据库
     *
     * @param id 主键id
     * @return 返回实体对象T
     */
    T selectCacheById(Long id);

    /**
     *
     * @param ids
     * @return
     */
    List<T> selectCacheByIds(List<Long> ids);

}
