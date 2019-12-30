package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_user")
public class SysUser extends BaseIdTimeEntity<Long> {
}
