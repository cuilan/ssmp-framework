package cn.cuilan.ssmp.admin.security.service;

import cn.cuilan.ssmp.admin.security.domain.SysUserDetails;
import cn.cuilan.ssmp.entity.SysUser;
import cn.cuilan.ssmp.mapper.SysUserMapper;
import cn.cuilan.ssmp.redis.RedisUtils;
import cn.cuilan.ssmp.redis.SysUserRedisPrefix;
import cn.cuilan.ssmp.service.SysUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * SpringSecurity UserDetailsService 实现类
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
@Component
public class SysUserDetailsServiceImpl extends SysUserService implements UserDetailsService {

    // 允许登录失败次数
    @Value("${ssmp.sysUser.loginTimes}")
    private String allowLoginErrorTimes;

    /**
     * 加密工具
     */
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 检查登录错误次数
        checkLoginErrorCount(username);
        // 检查用户是否存在
        SysUser sysUser = getSysUserInfo(null, username);
        if (sysUser == null) {
            incrLoginErrorCount(username);
            throw new UsernameNotFoundException("用户不存在");
        }
        return new SysUserDetails(sysUser);
    }

    @Override
    public SysUserDetails getSysUserInfo(Long sysUserId, String username) {
        SysUser sysUser = super.getSysUserInfo(sysUserId, username);
        return new SysUserDetails(sysUser);
    }

    /**
     * 校验密码是否正确
     *
     * @param userDetails 用户
     * @param password    密码
     */
    public void checkPassword(SysUserDetails userDetails, String password) {
        if (!new BCryptPasswordEncoder().matches(password, userDetails.getPassword())) {
            incrLoginErrorCount(userDetails.getUsername());
            throw new BadCredentialsException("密码错误");
        }
    }

    /**
     * 根据用户名检查登录错误次数
     *
     * @param username 用户名
     */
    private void checkLoginErrorCount(String username) {
        String errorCount = redisUtils.getString(SysUserRedisPrefix.LOGIN_ERROR, username);
        if (!StringUtils.isEmpty(errorCount) && Integer.parseInt(errorCount) >= Integer.parseInt(allowLoginErrorTimes)) {
            throw new LockedException("登录次数超过限制");
        }
    }

    /**
     * 登录错误计数
     *
     * @param username 用户名
     */
    private void incrLoginErrorCount(String username) {
        redisUtils.incr(SysUserRedisPrefix.LOGIN_ERROR, username);
    }

    /**
     * 修改密码
     *
     * @param sysUser 系统用户
     * @param oldPass 旧密码
     * @param newPass 新密码
     */
    @Transactional
    public void resetPassword(SysUser sysUser, String oldPass, String newPass) {
        SysUser dbUser = sysUserMapper.selectById(sysUser.getId());
        Assert.isTrue(bCryptPasswordEncoder.matches(oldPass, dbUser.getPassword()), "旧密码不正确");

        dbUser.setPassword(bCryptPasswordEncoder.encode(newPass));
        // 临时密码作废
        dbUser.setTrashTmpPwd(true);
        sysUserMapper.updateById(dbUser);
    }
}
