package cn.cuilan.observer.handler;

import cn.cuilan.common.BaseObservableEntity;

/**
 * 通用创建实体处理接口
 *
 * @param <T> 实体
 */
public interface CreateHandler<T extends BaseObservableEntity> extends DataHandler<T> {
}
