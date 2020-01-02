package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.common.BaseNoIdEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统权限与系统角色关联实体
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_sys_permission_roles")
public class SysPermissionRoles extends BaseNoIdEntity {

    private long rolesId;

    private long sysPermissionId;

    public SysPermissionRoles() {
    }

    public SysPermissionRoles(long rolesId, long sysPermissionId) {
        this.rolesId = rolesId;
        this.sysPermissionId = sysPermissionId;
    }
}
