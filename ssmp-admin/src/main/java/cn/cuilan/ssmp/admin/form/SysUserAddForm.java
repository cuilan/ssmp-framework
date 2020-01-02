package cn.cuilan.ssmp.admin.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class SysUserAddForm {

    /**
     * 账户名, 英文字母数字，至少四位
     */
    @NotEmpty(message = "账户名不能为空")
    @Length(min = 4, message = "账户名长度至少四位")
    @Pattern(regexp = "^[a-z0-9A-Z]+$", message = "账户名只能是英文字母和数字")
    private String username;

    /**
     * 姓名, 至少两个字符
     */
    @NotEmpty(message = "姓名不能为空")
    @Length(min = 2, message = "姓名长度至少两位")
    private String fullName;

    /**
     * 预留信息
     */
    @NotEmpty(message = "预留信息不能为空")
    private String reservedInfo;

    /**
     * 手机号
     */
    @Pattern(regexp = "^[1][3,4,5,7,8][0-9]{9}$", message = "请输入正确的手机号")
    private String phone;

    /**
     * 备注
     */
    private String notes;

    /**
     * 角色ID列表
     */
    @NotNull(message = "角色不能为空")
    @Size(min = 1, message = "至少要选一个角色")
    private List<Long> roles;

}
