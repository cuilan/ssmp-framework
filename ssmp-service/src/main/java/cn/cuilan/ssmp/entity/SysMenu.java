package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_menu")
public class SysMenu extends BaseIdTimeEntity<Long> {

    @NotNull(message = "菜单名称不能为空")
    private String name;

    @NotNull(message = "菜单URL不能为空")
    private String url;

    private String icon;

    private Long parentId;

    @NotNull(message = "菜单排序不能为空")
    private Integer priority;

    private String note;

    private Boolean visible;

    @TableField(exist = false)
    private List<SysMenu> subSysMenus;

    // 是否拥有，用于动态设置是否勾选
    @TableField(exist = false)
    private boolean retain;

}
