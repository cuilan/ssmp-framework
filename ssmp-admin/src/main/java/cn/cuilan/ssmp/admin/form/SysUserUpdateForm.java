package cn.cuilan.ssmp.admin.form;

import cn.cuilan.ssmp.admin.annotation.EnumValid;
import cn.cuilan.ssmp.enums.SysUserStatusEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SysUserUpdateForm {

    @NotNull(message = "不能为空")
    private Long uid;

    @EnumValid(clazz = SysUserStatusEnum.class, method = "getValue", message = "值错误[0, 1]", allowNull = true)
    private Integer status;

    private String phone;

    private String email;

    private String fullName;

    private String notes;

    /**
     * 角色ID列表
     */
    private List<Long> roles;
}
