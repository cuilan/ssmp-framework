package cn.cuilan.ssmp.common;

import java.io.Serializable;

public abstract class BaseObservableEntity<ID extends Serializable> implements Cloneable {

    public abstract ID getId();

    @Override
    protected BaseObservableEntity clone() throws CloneNotSupportedException {
        try {
            return (BaseObservableEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
