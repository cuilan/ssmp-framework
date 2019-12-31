package cn.cuilan.ssmp.redis;

import java.util.concurrent.TimeUnit;

/**
 * Redis key前缀
 *
 * @author zhang.yan
 */
public interface IRedisPrefix {

    String APP = "ssmp_";

    /**
     * 获取前缀
     */
    String getPrefix();

    /**
     * 获取key类型
     */
    RedisDataType getType();

    /**
     * 获取过期时间
     */
    int getExpire();

    /**
     * 获取过期单位
     */
    TimeUnit getExpireUnit();

    /**
     * 命名空间，每个子类需设置一个
     */
    String getNamespace();

    /**
     * 获取key全名称
     *
     * @param key 简单名称
     * @return 全名称
     */
    default String getFullKey(String key) {
        return APP + getNamespace() + ":" + getPrefix() + ":" + key;
    }
}
