package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_permission")
public class SysPermission extends BaseIdTimeEntity<Long> {

    private String name;

    private String description;

    private Boolean visible;

    // 是否拥有，用于动态设置是否勾选
    @TableField(exist = false)
    private boolean retain;
}
