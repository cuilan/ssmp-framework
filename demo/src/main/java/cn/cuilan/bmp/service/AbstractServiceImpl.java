package cn.cuilan.bmp.service;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

public abstract class AbstractServiceImpl<M extends BaseMapper<T>, T>
        extends ServiceImpl<M, T> implements IBaseService<T> {
}
