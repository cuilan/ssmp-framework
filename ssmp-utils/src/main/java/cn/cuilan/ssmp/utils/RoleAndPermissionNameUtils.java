package cn.cuilan.ssmp.utils;

/**
 * 角色、权限名称校验工具
 *
 * @author zhang.yan
 * @date 2020-01-02
 */
public class RoleAndPermissionNameUtils {

    // 权限名称连接符
    private final static String PERMISSION_JOINER = "_";

    /**
     * 权限或角色名称规则校验
     *
     * @param name 权限名称 或 角色名称
     */
    public static void checkName(String name) {
        if (name.contains(PERMISSION_JOINER)) {
            String[] per = name.split(PERMISSION_JOINER);
            if (per.length == 2) {
                return;
            }
        }
        throw new RuntimeException("权限名称格式不正确，格式：XXX_XXX");
    }

}
