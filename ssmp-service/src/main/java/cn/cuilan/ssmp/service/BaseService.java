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

    private static final int BATCH_SIZE = 1000;

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
    public T getCache(Long id) {
        supportCachedOperation();
        // noinspection unchecked
        T t = ((CachedMapper<T>) baseMapper).selectCacheById(id);
        if (t == null) {
            throw new BaseException("id not exist.");
        }
        return t;
    }

    @Override
    public boolean saveBatch(Collection<T> entityList) {
        return this.saveBatch(entityList, BATCH_SIZE);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        supportBatchOperation();
        // noinspection unchecked
        return ((CommonMapper<T>) baseMapper).saveBatch(entityList, batchSize);
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        return this.updateBatchById(entityList, BATCH_SIZE);
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        supportBatchOperation();
        // noinspection unchecked
        return ((CommonMapper<T>) baseMapper).updateBatchById(entityList, batchSize);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        return this.saveOrUpdateBatch(entityList, BATCH_SIZE);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        supportBatchOperation();
        // noinspection unchecked
        return ((CommonMapper<T>) baseMapper).saveOrUpdateBatch(entityList, batchSize);
    }

    /**
     * 检查类型，仅 CommonMapper 具备批量操作的能力
     */
    private void supportBatchOperation() {
        if (!(baseMapper instanceof CommonMapper)) {
            throw new BaseException(baseMapper.getClass().getSimpleName() + "Mapper 未继承 CommonMapper, 不支持批量操作");
        }
    }

    /**
     * 检查类型，仅 CachedMapper 具备缓存操作的能力
     */
    private void supportCachedOperation() {
        if (!(baseMapper instanceof CachedMapper)) {
            throw new BaseException(baseMapper.getClass().getSimpleName() + "Mapper 未继承 CachedMapper, 不支持缓存操作");
        }
    }
}
