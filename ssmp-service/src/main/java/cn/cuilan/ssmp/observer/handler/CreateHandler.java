package cn.cuilan.ssmp.observer.handler;

import cn.cuilan.ssmp.common.BaseObservableEntity;

/**
 * 通用创建实体处理接口
 *
 * @param <T> 实体
 */
public interface CreateHandler<T extends BaseObservableEntity<Long>> extends DataHandler<T> {
}
