package cn.cuilan.ssmp.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseIdEntity<ID extends Serializable> extends BaseObservableEntity<ID> implements Cloneable {

    /**
     * 子类继承此ID
     * <p>
     * 主键自增策略为AUTO
     */
    @Id
    @TableId(type = IdType.AUTO)
    protected ID id;

    @Override
    protected BaseIdEntity clone() throws CloneNotSupportedException {
        return (BaseIdEntity) super.clone();
    }
}
