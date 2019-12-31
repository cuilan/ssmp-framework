package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import cn.cuilan.ssmp.enums.OperationType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_operation_log")
public class SysOperationLog extends BaseIdTimeEntity<Long> {

    // 操作类型
    private OperationType operationType;

    // 操作详细描述
    private String description;

    // 操作人
    private Long sysUserId;

    // 操作对象的calss类型，如：修改角色名称，此字段值为：SysRole
    private String classType;

    // 被操作id，被操作对象的id
    private Long operatedId;

    @TableField(exist = false)
    private String sysUserName;

    @TableField(exist = false)
    private String operationName;

    public SysOperationLog() {
    }

    /**
     * 创建操作日志
     *
     * @param operationType 操作类型
     * @param description   常规操作可以直接获取该类型的描述，特殊操作也可以自定义详细的描述信息
     * @param sysUserId     操作人
     * @param classType     被操作对象的class类型
     * @param operatedId    被操作对象的id
     */
    public SysOperationLog(OperationType operationType,
                           String description,
                           Long sysUserId,
                           String classType,
                           Long operatedId) {
        this.operationType = operationType;
        this.description = description;
        this.sysUserId = sysUserId;
        this.classType = classType;
        this.operatedId = operatedId;
    }
}

