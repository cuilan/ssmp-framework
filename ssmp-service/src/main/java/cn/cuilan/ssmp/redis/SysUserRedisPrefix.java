package cn.cuilan.ssmp.redis;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * 系统用户Redis key 前缀及命名空间
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
@Getter
public enum SysUserRedisPrefix implements IRedisPrefix {

    // admin SysUser token 过期时间为一天
    TOKEN("token", RedisDataType.KV, 1, TimeUnit.DAYS),

    // 系统用户登录错误次数记录，过期时间为3分钟
    LOGIN_ERROR("login_error", RedisDataType.KV, 3, TimeUnit.MINUTES);

    private String prefix;
    private RedisDataType type;
    private int expire;
    private TimeUnit expireUnit;

    SysUserRedisPrefix(String prefix, RedisDataType type, int expire, TimeUnit expireUnit) {
        this.prefix = prefix;
        this.type = type;
        this.expire = expire;
        this.expireUnit = expireUnit;
    }

    @Override
    public String getNamespace() {
        return "sysuser";
    }
}
