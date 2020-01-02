package cn.cuilan.ssmp.enums;

/**
 * 系统用户状态
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
public enum SysUserStatusEnum {

    // 禁用
    DISABLED(0, "停用"),

    // 启用
    ACTIVATED(1, "正常");

    public final Integer value;

    public final String desc;

    SysUserStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
