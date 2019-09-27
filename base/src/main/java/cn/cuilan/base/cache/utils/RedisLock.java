package cn.cuilan.base.cache.utils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

/**
 * RedisLock
 *
 * @author zhang.yan
 */
@Slf4j
public class RedisLock {

    private static JedisPool jedisPool;

    private static int maxRetry = 300;

    // 初始化
    public static void init(JedisPool jedisPool) {
        RedisLock.jedisPool = jedisPool;
    }

    public static boolean setIfAbsent(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setnx(key, "1") == 1;
        }
    }

    /**
     * 加锁
     *
     * @param key key
     * @return 返回Lock对象
     */
    public static Lock lock(Object key) {
        String lockKey = getLockKey(key.toString());
        int retry = 0;
        while (!setIfAbsent(lockKey)) {
            retry++;
            try {
                TimeUnit.MICROSECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (retry >= maxRetry) {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.del(lockKey);
                }
            }
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(lockKey, 3);
        }
        return new Lock(lockKey);
    }

    /**
     * 对key加锁
     */
    private static String getLockKey(String key) {
        return key + "_LOCK";
    }

    /**
     * 释放锁
     *
     * @param key key
     */
    public static void unlock(Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(getLockKey(key.toString()));
        }
    }

    /**
     * 对某个key加锁
     *
     * @param key key
     * @return 返回Lock对象
     */
    public static Lock lockOne(String key) {
        String lockKey = getLockKey(key);
        try (Jedis jedis = jedisPool.getResource()) {
            Long nx = jedis.setnx(lockKey, "1");
            if (nx != 1) {
                log.warn("can not get lock: [key = {}, lockKey = {}, redisValue = {}, redisTTL = {}]",
                        key, lockKey, jedis.get(lockKey), jedis.ttl(lockKey));
                return null;
            }
            jedis.expire(lockKey, 3);
            return new Lock(lockKey);
        }
    }

    /**
     * Redis锁封装
     */
    public static class Lock implements AutoCloseable {

        String lockKey;

        private Lock(String lockKey) {
            this.lockKey = lockKey;
        }

        @Override
        public void close() {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.del(lockKey);
                log.debug("release lock: [key = {}]", lockKey);
            }
        }
    }

}
