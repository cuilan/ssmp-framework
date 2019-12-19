package cn.cuilan.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseIdTimeEntity<ID extends Serializable> extends BaseIdEntity<ID> {

    protected Long createTime;

    protected Long updateTime;

    public LocalDateTime getCreateDateTime() {
        return toLocalDateTime(createTime);
    }

    public LocalDateTime getUpateDateTime() {
        return toLocalDateTime(updateTime);
    }

    public LocalDateTime toLocalDateTime(Long time) {
        if (time == null) {
            return null;
        }
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    protected BaseIdTimeEntity clone() throws CloneNotSupportedException {
        return (BaseIdTimeEntity) super.clone();
    }
}
