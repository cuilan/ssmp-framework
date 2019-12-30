package cn.cuilan.ssmp.observer.handler;

import cn.cuilan.ssmp.common.BaseObservableEntity;

/**
 * 通用数据处理接口
 *
 * @param <T> 实体
 */
public interface DataHandler<T extends BaseObservableEntity> {

    /**
     * 执行处理方法
     *
     * @param obj T类型对象
     */
    void handler(T obj);

}
