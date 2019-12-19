package cn.cuilan.observer.handler;

import cn.cuilan.common.BaseObservableEntity;

/**
 * 通用更新实体处理接口
 *
 * @param <T> 实体
 */
public interface UpdateHandler<T extends BaseObservableEntity> {

    /**
     * 执行更新处理方法
     *
     * @param oldObj 旧对象
     * @param newObj 新对象
     */
    void handler(T oldObj, T newObj);

}
