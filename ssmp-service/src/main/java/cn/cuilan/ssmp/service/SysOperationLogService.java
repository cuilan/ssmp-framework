package cn.cuilan.ssmp.service;

import cn.cuilan.ssmp.entity.SysOperationLog;
import cn.cuilan.ssmp.enums.OperationType;
import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.mapper.SysOperationLogMapper;
import cn.cuilan.ssmp.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SysOperationLogService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    /**
     * 获取系统操作日志
     *
     * @param sysUserId     系统用户ID
     * @param hasPermission 操作权限
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param pageNum       页数
     * @param pageSize      每页数量
     * @return 操作日志记录
     */
    public List<SysOperationLog> getSysOperationLogs(Long sysUserId, boolean hasPermission,
                                                     Date startTime, Date endTime,
                                                     int pageNum, int pageSize) {
        // 无权限时，当前登录用户id不能为空
        if (!hasPermission && sysUserId == null) {
            throw new BaseException("无权限访问");
        }
        List<SysOperationLog> logs = sysOperationLogMapper.getOperationLogsByAdmin(sysUserId, startTime.getTime(), endTime.getTime(), pageNum, pageSize);
        logs.forEach(log -> {
            log.setSysUserName(sysUserMapper.selectById(log.getSysUserId()).getUsername());
            log.setOperationName(log.getOperationType().getDesc());
        });
        return logs;
    }


    /**
     * 写系统操作日志
     *
     * @param userId      用户ID
     * @param type        操作类型
     * @param description 描述
     * @param classType   类名
     * @param operatedId  操作对象ID
     */
    public void logSysOperation(Long userId, OperationType type, String description, String classType, Long operatedId) {
        sysOperationLogMapper.insert(new SysOperationLog(type, description,
                userId, classType, operatedId));
    }
}
