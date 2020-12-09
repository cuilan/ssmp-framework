package cn.cuilan.ssmp.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * ID、createTime、updateTime基础实体抽象类
 *
 * @param <ID> 实体ID
 * @author zhang.yan
 * @date 2019-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseIdTimeEntity<ID extends Serializable> extends BaseIdEntity<ID> {

    /**
     * 创建时间，Long类型时间戳
     */
    protected Long createTime;

    /**
     * 更新时间，Long类型时间戳
     */
    protected Long updateTime;

    /**
     * 创建时间戳 -> LocalDateTime 转换
     *
     * @return LocalDateTime类型的创建时间
     */
    public LocalDateTime getCreateDateTime() {
        return toLocalDateTime(createTime);
    }

    /**
     * 更新时间戳 -> LocalDateTime 转换
     *
     * @return LocalDateTime类型的更新时间
     */
    public LocalDateTime getUpateDateTime() {
        return toLocalDateTime(updateTime);
    }

    /**
     * 时间戳 -> LocalDateTime 转换，提供其他时间字段的转换
     *
     * @param time 时间戳
     * @return LocalDateTime类型的时间
     */
    public LocalDateTime toLocalDateTime(Long time) {
        if (time == null) {
            return null;
        }
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    protected BaseIdTimeEntity<ID> clone() throws CloneNotSupportedException {
        return (BaseIdTimeEntity<ID>) super.clone();
    }
}
