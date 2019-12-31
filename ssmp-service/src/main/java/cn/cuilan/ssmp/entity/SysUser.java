package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_user")
public class SysUser extends BaseIdTimeEntity<Long> {

    // 用户名，唯一
    protected String username;

    @JSONField(serialize = false)
    protected String password;

    // 是否是超级管理员
    protected boolean isAdmin;

    // 状态 0-停用 1-正常
    protected int status;

    // 全名
    protected String fullName;

    // 头像
    protected String portrait;

    // 手机号，唯一
    protected String phone;

    protected String email;

    // 预留信息
    protected String reservedInfo;

    // 临时密码
    protected String tmpPwd;

    // 废弃临时密码(0-未作废,1-已作废)
    @JSONField(serialize = false)
    protected boolean trashTmpPwd;

    // 备注
    protected String notes;

    // 添加人
    @JSONField(serialize = false)
    protected Long createdBy;

    protected String lastLoginIp;

    protected Long lastLoginTime;

    // ============ Transient ===========

    @TableField(exist = false)
    protected List<SysRole> roles;

    @TableField(exist = false)
    protected List<SysPermission> permissions;

    @TableField(exist = false)
    protected List<SysMenu> menus;
}
