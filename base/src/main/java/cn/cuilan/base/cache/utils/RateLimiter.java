package cn.cuilan.base.cache.utils;

import org.apache.commons.lang3.time.DateUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.Date;
import java.util.List;

public class RateLimiter {

    private static final String KEY_PREFIX = "rateLimiter:";
    private JedisPool jedisPool;
    private long maxCount;
    private int periodSeconds;
    // 命名空间
    private String nameSpace;

    public RateLimiter(JedisPool jedisPool, long maxCount, int periodSeconds, String nameSpace) {
        this.jedisPool = jedisPool;
        this.maxCount = maxCount;
        this.periodSeconds = periodSeconds;
        this.nameSpace = nameSpace;
        if (maxCount < 0 || periodSeconds < 0) {
            throw new IllegalArgumentException("count and period should not be negative.");
        }
    }

    private String getRemoteKey(String key) {
        return KEY_PREFIX + nameSpace + ":" + key;
    }

    public boolean rateLimited(String key, boolean updateTimestamp) {
        if (maxCount == 0) {
            return true;
        }
        String remoteKey = getRemoteKey(key);
        String lockKey = remoteKey + "_LOCK";
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.setnx(lockKey, "1") != 1) {
                return true;
            }
        }
        try (Jedis jedis = jedisPool.getResource()) {
            try {
                if (isUnderLimit(jedis, remoteKey)) {
                    if (updateTimestamp) {
                        updateTimestamps(jedis, remoteKey);
                    }
                    return false;
                }
                return true;
            } finally {
                jedis.del(lockKey);
            }
        }
    }

    public void updateTimestamp(String key) {
        String remoteKey = getRemoteKey(key);
        String lockKey = remoteKey + "_LOCK";
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.setnx(lockKey, "1") != 1) {
                return;
            }
        }
        try (Jedis jedis = jedisPool.getResource()) {
            try {
                updateTimestamps(jedis, remoteKey);
            } finally {
                jedis.del(lockKey);
            }
        }
    }

    private boolean isUnderLimit(Jedis jedis, String key) {
        long length = jedis.llen(key);
        if (length < maxCount) {
            return true;
        }
        List<String> timestamps = jedis.lrange(key, -1, -1);
        if (timestamps.isEmpty()) {
            return true;
        }
        Date oldest = new Date(Long.valueOf(timestamps.get(0)));
        return DateUtils.addSeconds(oldest, periodSeconds).before(new Date());
    }

    /**
     * 跟新时间戳
     */
    private void updateTimestamps(Jedis jedis, String key) {
        Pipeline pipeline = jedis.pipelined();
        pipeline.lpush(key, String.valueOf(System.currentTimeMillis()));
        pipeline.ltrim(key, 0, maxCount - 1);
        pipeline.expire(key, periodSeconds * 2);
        pipeline.sync();
    }

    public void reset(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String remoteKey = getRemoteKey(key);
            jedis.del(remoteKey);
        }
    }

    public Long lastTime(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            key = getRemoteKey(key);
            List<String> timestamps = jedis.lrange(key, -1, -1);
            if (CollectionUtils.isEmpty(timestamps)) {
                return null;
            }
            return Long.parseLong(timestamps.get(0));
        }
    }

    public Long getDiffMinuteByNextFree(String key) {
        Long beforeTime = lastTime(key);
        if (beforeTime == null) {
            return 1L;
        }
        long diff = periodSeconds * 1000 - (System.currentTimeMillis() - beforeTime);
        if (diff <= 0) {
            return 1L;
        }
        return (long) Math.ceil((float) diff / 60000);
    }
}
