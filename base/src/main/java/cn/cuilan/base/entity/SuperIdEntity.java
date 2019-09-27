package cn.cuilan.base.entity;

import java.io.Serializable;

public interface SuperIdEntity<ID> extends Serializable {
    ID getId();
}
