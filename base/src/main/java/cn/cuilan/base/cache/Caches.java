package cn.cuilan.base.cache;

import cn.cuilan.base.cache.event.CacheEventCollector;
import cn.cuilan.base.cache.event.CacheEventEnum;
import cn.cuilan.base.cache.scenario.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Caches {

    public static Map<String, AbstractCache> cacheMap = new HashMap<>(0);
    public static Map<String, LocalCache> localCacheMap = new HashMap<>(0);
    private static CacheEventCollector eventCollector;

    public static <T> PageCacheBuilder<T> forPage(String namespace, Class<T> itemClass) {
        return new PageCacheBuilder(namespace);
    }

    public static <T> SortedSetCacheBuilder<T> forSortedSet(String namespace, Class<T> idClass) {
        return new SortedSetCacheBuilder<>(namespace);
    }

    public static CountCacheBuilder forCount(String namespace) {
        return new CountCacheBuilder(namespace);
    }

    public static <K, V> ValueCacheBuilder<K, V> forValue(String namespace, Class<K> keyClass, Class<V> valueClass) {
        return new ValueCacheBuilder<>(namespace, keyClass, valueClass);
    }

    public static <K, T> ListCacheBuilder<K, T> forList(String namespace, Class<K> keyClass, Class<T> valueClass) {
        return new ListCacheBuilder<>(namespace);
    }

    public static List<CacheEventCollector.CacheStatisticVo> getEventStatistic() {
        return eventCollector.show();
    }

    static void setEventCollector(long showStatisticPeriod) {
        eventCollector = new CacheEventCollector(cacheMap.keySet(), showStatisticPeriod);
    }

    public static void collectEvent(String namespace, CacheEventEnum eventEnum, double value) {
        if (eventCollector != null) {
            eventCollector.collect(namespace, eventEnum, value);
        }
    }
}
