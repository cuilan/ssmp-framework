package cn.cuilan.ssmp.admin.controller;

import cn.cuilan.ssmp.admin.annotation.Logined;
import cn.cuilan.ssmp.admin.security.domain.SysUserDetails;
import cn.cuilan.ssmp.service.SysOperationLogService;
import cn.cuilan.ssmp.utils.result.Result;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统操作日志
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
@RestController
public class SysOperationLogController {

    @Resource
    private SysOperationLogService sysOperationLogService;

    /**
     * 查询系统操作日志
     */
    @GetMapping("/api/admin/log/query")
    @PreAuthorize("authenticated and hasPermission('log', 'query')")
    public Result getOperationLogs(@RequestParam(value = "uid", required = false) Long sysUserId,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "st", required = false) Date startTime,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "et", required = false) Date endTime,
                                   @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                   @Logined SysUserDetails loginUser) {
        if (endTime == null) {
            endTime = new Date();
        }
        if (startTime == null) {
            // 默认查询近7天的操作日志
            startTime = DateUtils.addDays(new Date(), -7);
        }
        List<String> authorities = new ArrayList<>();
        boolean hasPermission = false;
        // 超级管理员、管理员可以查看所有操作记录
        loginUser.getAuthorities().forEach(auth -> authorities.add(auth.getAuthority()));
        if (authorities.contains("ROLE_ROOT") || authorities.contains("ROLE_ADMIN")) {
            hasPermission = true;
            return Result.success(sysOperationLogService.getSysOperationLogs(sysUserId, hasPermission, startTime, endTime, pageNum, pageSize));
        }
        return Result.success(sysOperationLogService.getSysOperationLogs(loginUser.getId(), hasPermission, startTime, endTime, pageNum, pageSize));
    }

}
