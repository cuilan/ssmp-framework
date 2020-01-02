package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.common.BaseNoIdEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户与系统角色关联实体
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_sys_user_roles")
public class SysUserRoles extends BaseNoIdEntity {

    private long rolesId;

    private long sysUserId;

    public SysUserRoles(Long rolesId, Long sysUserId) {
        this.rolesId = rolesId;
        this.sysUserId = sysUserId;
    }
}
