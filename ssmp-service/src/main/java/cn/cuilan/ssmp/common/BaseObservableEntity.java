package cn.cuilan.ssmp.common;

import java.io.Serializable;

/**
 * 可被观察的实体基础抽象类
 *
 * @param <ID> 实体ID
 * @author zhang.yan
 * @date 2019-12-31
 */
public abstract class BaseObservableEntity<ID extends Serializable> implements Observable, Cloneable {

    /**
     * 实体ID的 get 方法
     *
     * @return 返回泛型ID
     */
    public abstract ID getId();

    @Override
    protected BaseObservableEntity<ID> clone() throws CloneNotSupportedException {
        try {
            // noinspection unchecked
            return (BaseObservableEntity<ID>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
