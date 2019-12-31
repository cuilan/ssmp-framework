package cn.cuilan.ssmp.enums;

/**
 * 操作日志类型
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
public enum OperationType {

    // 系统用户操作
    USER_LOGIN("USER_LOGIN", "系统用户登录"),
    USER_RESET_PASSWORD("USER_RESET_PASSWORD", "系统用户修改密码"),
    USER_ADD("USER_ADD", "添加系统用户"),
    USER_UPDATE("USER_UPDATE", "修改系统用户"),
    USER_INVISIBLE("USER_INVISIBLE", "禁用系统用户"),
    USER_ACTIVATE("USER_ACTIVATE", "激活系统用户"),

    // 角色操作
    ROLE_ADD("ROLE_ADD", "添加系统角色"),
    ROLE_UPDATE("ROLE_UPDATE", "修改系统角色"),
    ROLE_DELETE("ROLE_DELETE", "删除系统角色"),
    ROLE_USER_RELATION("ROLE_USER_RELATION", "为用户关联角色"),
    ROLE_USER_DELETE_RELATION("ROLE_USER_DELETE_RELATION", "删除用户的角色"),
    ROLE_PERMISSION_RELATION("ROLE_PERMISSION_RELATION", "为权限关联角色"),
    ROLE_PERMISSION_DELETE_RELATION("ROLE_PERMISSION_DELETE_RELATION", "删除权限与角色的关联"),
    ROLE_MENU_RELATION("ROLE_MENU_RELATION", "为菜单关联角色"),
    ROLE_MENU_DELETE_RELATION("ROLE_MENU_DELETE_RELATION", "删除菜单与角色的关联"),

    // 权限操作
    PERMISSION_ADD("PERMISSION_ADD", "添加系统权限"),
    PERMISSION_UPDATE("PERMISSION_UPDATE", "修改系统权限"),
    PERMISSION_DELETE("PERMISSION_DELETE", "删除系统权限"),

    // 菜单操作
    MENU_ADD("MENU_ADD", "添加系统菜单"),
    MENU_UPDATE("MENU_UPDATE", "修改系统菜单"),
    MENU_DELETE("MENU_DELETE", "删除系统菜单"),
    MENU_SORT("MENU_SORT", "修改系统菜单排序"),
    MENU_RELATION("MENU_RELATION", "修改系统菜单父子级关系");

    private String value;

    // 描述
    private String desc;

    OperationType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
