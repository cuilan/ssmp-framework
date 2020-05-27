package cn.cuilan.ssmp.redis;

import cn.cuilan.ssmp.entity.User;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @author zhang.yan
 * @date 2020/5/27
 */
@Getter
public enum EntityRedisPrefix implements IRedisPrefix {

    // 用户实体缓存key
    USER(User.class.getName(), RedisDataType.KV, 1, TimeUnit.DAYS, 1);

    private String prefix;
    private RedisDataType type;
    private int expire;
    private TimeUnit expireUnit;
    private int version;

    EntityRedisPrefix(String prefix, RedisDataType type, int expire, TimeUnit expireUnit, int version) {
        this.prefix = prefix;
        this.type = type;
        this.expire = expire;
        this.expireUnit = expireUnit;
        this.version = version;
    }

    @Override
    public String getNamespace() {
        return "entity";
    }

    @Override
    public String getFullKey(String key) {
        return APP + ":" + getNamespace() + ":" + getPrefix() + ":" + getVersion() + ":" + key;
    }


}
