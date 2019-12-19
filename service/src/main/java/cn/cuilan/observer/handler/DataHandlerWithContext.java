package cn.cuilan.observer.handler;

import cn.cuilan.common.BaseObservableEntity;
import cn.cuilan.observer.ObserverContext;

/**
 * 通用数据处理接口
 *
 * @param <T> 实体
 */
public interface DataHandlerWithContext<T extends BaseObservableEntity> {

    /**
     * 执行处理方法
     *
     * @param obj     T类型对象
     * @param context 观察者对象上下文
     */
    void handler(T obj, ObserverContext context);

}
