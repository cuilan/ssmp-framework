package cn.cuilan.ssmp.observer.handler;

import cn.cuilan.ssmp.common.BaseObservableEntity;

/**
 * 通用删除实体处理接口
 *
 * @param <T> 实体
 */
public interface DeleteHandler<T extends BaseObservableEntity> extends DataHandler<T> {
}
