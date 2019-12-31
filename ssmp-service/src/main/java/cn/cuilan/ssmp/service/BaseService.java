package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.exception.BaseException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 抽象Service封装
 *
 * @param <M> 实体Mapper
 * @param <T> 实体
 * @author zhang.yan
 * @date 2019-12-31
 */
public abstract class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * 根据id查询实体，可扩展添加缓存
     *
     * @param id Longoing类型id
     * @return 返回实体T
     */
    public T getNotNull(Long id) {
        T t = baseMapper.selectById(id);
        if (t == null) {
            throw new BaseException("id not exsit.");
        }
        return t;
    }

}
