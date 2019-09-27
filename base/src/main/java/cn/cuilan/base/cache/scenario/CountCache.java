package cn.cuilan.base.cache.scenario;

public class CountCache extends ValueCache<String, Integer> {

    public void incrBy(String key, Integer num) {
        update(key, rawKey ->
                remoteCache.incrBy(rawKey, num)
        );
    }

}
