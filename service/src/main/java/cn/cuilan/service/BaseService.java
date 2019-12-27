package cn.cuilan.service;

import cn.cuilan.exception.BaseException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

public abstract class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public T getNotNull(Long id) {
        T t = baseMapper.selectById(id);
        if (t == null) {
            throw new BaseException("id not exsit.");
        }
        return t;
    }

}
