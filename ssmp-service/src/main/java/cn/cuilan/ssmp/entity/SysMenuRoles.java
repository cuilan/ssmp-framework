package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.common.BaseNoIdEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单与系统角色关联实体
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_sys_menu_roles")
public class SysMenuRoles extends BaseNoIdEntity {

    private long rolesId;

    private long sysMenuId;

    public SysMenuRoles() {
    }

    public SysMenuRoles(long rolesId, long sysMenuId) {
        this.rolesId = rolesId;
        this.sysMenuId = sysMenuId;
    }
}
