package cn.cuilan.base.cache;

import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

public class CacheConfig {

    private JedisPool jedisPool;

    public CacheConfig(String remoteCachePrefix, JedisPool jedisPool, String mqHost, int mqPort, String username, String password) {
        this.jedisPool = jedisPool;
        AbstractCache.remoteCachePrefix = remoteCachePrefix;
        AbstractCache.remoteCache = new RemoteCache(jedisPool);
        LocalCache.localCacheSynchronizer = new LocalCacheSynchronizer(remoteCachePrefix, mqHost, mqPort, username, password);
    }

    public void showStatistic(int period, TimeUnit timeUnit) {
        Caches.setEventCollector(timeUnit.toSeconds(period));
    }

}
