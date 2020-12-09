package cn.cuilan.ssmp.observer.handler;

import cn.cuilan.ssmp.common.BaseObservableEntity;
import cn.cuilan.ssmp.observer.ObserverContext;

/**
 * 通用更新实体处理接口
 *
 * @param <T> 实体
 */
public interface UpdateHandlerWithContext<T extends BaseObservableEntity<Long>> {

    /**
     * 更新执行处理方法
     *
     * @param oldObj  旧对象
     * @param newObj  新对象
     * @param context 观察者对象上下文
     */
    void handler(T oldObj, T newObj, ObserverContext<T> context);

}
