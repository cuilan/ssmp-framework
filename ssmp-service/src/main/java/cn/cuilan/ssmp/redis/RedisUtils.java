package cn.cuilan.ssmp.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具
 *
 * @author zhang.yan
 */
@Component
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 保存key-value
     *
     * @param redisPrefix IRedisPrefix的实现子类
     * @param key         Number类型，key
     * @param value       String类型，value
     */
    public void saveString(IRedisPrefix redisPrefix, Number key, String value) {
        saveString(redisPrefix, key.toString(), value);
    }

    /**
     * 保存key-value
     *
     * @param redisPrefix IRedisPrefix的实现子类
     * @param key         String类型，key
     * @param value       String类型，value
     */
    public void saveString(IRedisPrefix redisPrefix, String key, String value) {
        ValueOperations<String, String> vo = stringRedisTemplate.opsForValue();
        String fullKey = redisPrefix.getFullKey(key);
        vo.set(fullKey, value);
        if (redisPrefix.getType() != RedisDataType.KV) {
            throw new RuntimeException(String.format("Redis key RedisPrefix: %s type is: %s, can not use String-KV.",
                    redisPrefix.getPrefix(), redisPrefix.getType()));
        }
        if (redisPrefix.getExpire() > 0) {
            stringRedisTemplate.expire(fullKey, redisPrefix.getExpire(), redisPrefix.getExpireUnit());
        }
    }

    /**
     * key是否存在
     *
     * @param redisPrefix IRedisPrefix的实现子类
     * @param key         String类型，key
     * @return 是否存在
     */
    public Boolean existsKey(IRedisPrefix redisPrefix, String key) {
        if (redisPrefix.getType() != RedisDataType.KV) {
            throw new RuntimeException(String.format("Redis key RedisPrefix: %s type is: %s, can not use String-KV.",
                    redisPrefix.getPrefix(), redisPrefix.getType()));
        }
        return stringRedisTemplate.hasKey(redisPrefix.getFullKey(key));
    }

    /**
     * 计数，自增
     *
     * @param redisPrefix IRedisPrefix的实现子类
     * @param key         String类型，key
     * @return 返回自增后的值
     */
    public Long incr(IRedisPrefix redisPrefix, String key) {
        if (redisPrefix.getType() != RedisDataType.KV) {
            throw new RuntimeException(String.format("Redis key RedisPrefix: %s type is: %s, can not use String-KV.",
                    redisPrefix.getPrefix(), redisPrefix.getType()));
        }
        return stringRedisTemplate.opsForValue().increment(redisPrefix.getFullKey(key));
    }

    /**
     * 设置过期时间
     *
     * @param redisPrefix IRedisPrefix的实现子类
     * @param key         String类型，key
     * @param seconds     过期时间，单位：秒
     */
    public void expire(IRedisPrefix redisPrefix, String key, final int seconds) {
        if (seconds > 0) {
            stringRedisTemplate.expire(redisPrefix.getFullKey(key), seconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 根据key获取value
     *
     * @param redisPrefix IRedisPrefix的实现子类
     * @param key         String类型，key
     * @return 返回value
     */
    public String getString(IRedisPrefix redisPrefix, String key) {
        if (redisPrefix.getType() != RedisDataType.KV) {
            throw new RuntimeException(String.format("Redis key RedisPrefix: %s type is: %s, can not use String-KV.",
                    redisPrefix.getPrefix(), redisPrefix.getType()));
        }
        return stringRedisTemplate.opsForValue().get(redisPrefix.getFullKey(key));
    }

    /**
     * 删除key
     *
     * @param redisPrefix IRedisPrefix的实现子类
     * @param key         String类型，key
     * @return 是否删除成功
     */
    public Boolean deleteKey(IRedisPrefix redisPrefix, String key) {
        if (redisPrefix.getType() != RedisDataType.KV) {
            throw new RuntimeException(String.format("Redis key RedisPrefix: %s type is: %s, can not use String-KV.",
                    redisPrefix.getPrefix(), redisPrefix.getType()));
        }
        return stringRedisTemplate.delete(redisPrefix.getFullKey(key));
    }

    /**
     * 设置原生key-value，不强制过期时间，不推荐使用
     *
     * @param key   key
     * @param value value
     */
    @Deprecated
    public void saveString(String key, String value) {
        ValueOperations<String, String> vo = stringRedisTemplate.opsForValue();
        vo.set(key, value);
    }

    /**
     * 原生redis获取，不推荐使用
     *
     * @param key key
     * @return 返回value
     */
    @Deprecated
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

}
