package cn.cuilan.ssmp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

/**
 * 基础 Mapper 映射，提供 MyBatisPlus 增强方法
 *
 * @author zhang.yan
 * @date 2020/5/27
 */
@Mapper
public interface CommonMapper<T> extends BaseMapper<T> {

    /**
     * 批量保存
     *
     * @param entityList 实体集合
     * @return 返回是否保存成功
     */
    boolean saveBatch(Collection<T> entityList);

    /**
     * 批量保存
     *
     * @param entityList 实体集合
     * @param batchSize  批量条数，达到此值触发批量操作
     * @return 返回是否保存成功
     */
    boolean saveBatch(Collection<T> entityList, int batchSize);

    /**
     * 批量更新
     *
     * @param entityList 实体集合
     * @return 返回是否更新成功
     */
    boolean updateBatchById(Collection<T> entityList);

    /**
     * 批量更新
     *
     * @param entityList 实体集合
     * @param batchSize  批量条数，达到此值触发批量操作
     * @return 返回是否更新成功
     */
    boolean updateBatchById(Collection<T> entityList, int batchSize);

    /**
     * 批量保存或更新
     *
     * @param entityList 实体集合
     * @return 返回是否更新成功
     */
    boolean saveOrUpdateBatch(Collection<T> entityList);

    /**
     * 批量保存或更新
     *
     * @param entityList 实体集合
     * @param batchSize  批量条数，达到此值触发批量操作
     * @return 返回是否保存/更新成功
     */
    boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);

}
