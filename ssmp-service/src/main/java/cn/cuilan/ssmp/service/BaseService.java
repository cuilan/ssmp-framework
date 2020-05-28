package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.mapper.CachedMapper;
import cn.cuilan.ssmp.mapper.CommonMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 抽象Service封装
 *
 * @param <M> 实体Mapper
 * @param <T> 实体
 * @author zhang.yan
 * @date 2019-12-31
 */
@Service
public abstract class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * 根据id查询实体
     *
     * @param id Long类型id
     * @return 返回实体T
     */
    public T getNotNull(Long id) {
        T t = baseMapper.selectById(id);
        if (t == null) {
            throw new BaseException("id not exist.");
        }
        return t;
    }

    /**
     * 根据id从缓存查询实体
     *
     * @param id Long类型id
     * @return 返回实体T
     */
    public T getNotNullCache(Long id) {
        if (!(baseMapper instanceof CachedMapper)) {
            throw new BaseException(baseMapper.getClass().getSimpleName() + "Mapper 未继承 CachedMapper.");
        }
        T t = ((CachedMapper<T>) baseMapper).selectByIdCached(id);
        if (t == null) {
            throw new BaseException("id not exist.");
        }
        return t;
    }

    @Override
    public boolean saveBatch(Collection<T> entityList) {
        return this.saveBatch(entityList, 1000);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        checkClassType();
        return ((CommonMapper<T>) baseMapper).saveBatch(entityList, batchSize);
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        return this.updateBatchById(entityList, 1000);
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        checkClassType();
        return ((CommonMapper<T>) baseMapper).updateBatchById(entityList, batchSize);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        return this.saveOrUpdateBatch(entityList, 1000);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        checkClassType();
        return ((CommonMapper<T>) baseMapper).saveOrUpdateBatch(entityList, batchSize);
    }

    private void checkClassType() {
        if (!(baseMapper instanceof CommonMapper)) {
            throw new BaseException("未继承 CommonMapper, 不支持批量操作");
        }
    }
}
