package cn.cuilan.observer.handler;

import cn.cuilan.common.BaseObservableEntity;
import cn.cuilan.observer.ObserverContext;

/**
 * 通用更新实体处理接口
 *
 * @param <T> 实体
 */
public interface UpdateHandlerWithContext<T extends BaseObservableEntity> {

    /**
     * 更新执行处理方法
     *
     * @param oldObj  旧对象
     * @param newObj  新对象
     * @param context 观察者对象上下文
     */
    void handler(T oldObj, T newObj, ObserverContext context);

}
