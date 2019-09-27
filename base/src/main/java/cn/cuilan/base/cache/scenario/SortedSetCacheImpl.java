package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.core.BaseCacheExecutor;
import cn.cuilan.base.cache.core.CacheGetContext;
import cn.cuilan.base.cache.core.CacheInterceptor;
import cn.cuilan.base.cache.interceptor.LocalCacheInterceptor;
import cn.cuilan.base.cache.interceptor.NullValueInterceptor;
import cn.cuilan.base.cache.utils.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SortedSetCacheImpl<T> extends AbstractCache<String, SortedSetCache.Loader<T>> implements SortedSetCache<T> {

    private static String max_value = "+inf";
    private static String min_value = "-inf";
    Logger log = LoggerFactory.getLogger(SortedSetCacheImpl.class);
    PagingExecutor<List<T>> pagingExe;
    private Integer maxElements;
    private OutOfRangeGetter<T> outOfRangeGetter;

    @Override
    public void add(String key, T id, double score) {
        ensureCacheData(key);
        Map<T, Double> map = new HashMap<>();
        map.put(id, score);
        addAll(remoteKey(key), map);
    }

    @Override
    public void addAllByMap(String key, Map<T, Double> dataMap) {
        ensureCacheData(key);
        addAll(remoteKey(key), dataMap);
    }

    private void addAll(String key, Map<T, Double> dataMap) {
        boolean exist = remoteCache.exist(key);
        remoteCache.zAdd(key, dataMap);
        //如果添加之前不存在，则设置超时时间
        if (!exist) {
            remoteCache.expire(key, remoteExpireSecond, TimeUnit.SECONDS);
        }
        limitMaxElement(key);
        if (localCache != null) {
            localCache.clear();
        }
    }

    private void limitMaxElement(String rawKey) {
        if (maxElements != null) {
            //控制集合最大数量
            long currentSize = remoteCache.zCard(rawKey);
            if (currentSize > maxElements) {
                remoteCache.zremrangeByRank(rawKey, 0, currentSize - maxElements - 1);
            }
        }
    }

    private void ensureCacheData(String key) {
        ensureCacheData(key, loader);
    }

    private void ensureCacheData(String key, SortedSetCache.Loader loader) {
        String remoteKey = remoteKey(key);
        if (loader != null && !remoteCache.exist(remoteKey)) {
            loadData(key, remoteKey, loader);
        }
    }

    void setMaxElements(Integer maxSize) {
        this.maxElements = maxSize;
    }

    public void setOutOfRangeGetter(OutOfRangeGetter<T> outOfRangeGetter) {
        this.outOfRangeGetter = outOfRangeGetter;
    }

    /**
     * 加载数据
     *
     * @return
     */
    private void loadData(String key, String remoteKey, SortedSetCache.Loader loader) {
        String lockKey = remoteKey + "$$LOAD";
        if (remoteCache.setNX(lockKey, "1", 1, TimeUnit.SECONDS)) {
            long start = System.currentTimeMillis();
            Map<T, Double> dataMap = loader.load(key);
            if (!CollectionUtils.isEmpty(dataMap)) {
                log.info("缓存数据加载完毕[remoteKey={},duration={}ms]", remoteKey, System.currentTimeMillis() - start);
                addAll(remoteKey, dataMap);
                remoteCache.del(lockKey);
            }
        }
    }

    @Override
    public long size(String key) {
        ensureCacheData(key);
        return remoteCache.zCard(remoteKey(key));
    }

    @Override
    public void remove(String key, T id) {
        ensureCacheData(key);
        remoteCache.zrem(remoteKey(key), id);
        if (localCache != null) {
            localCache.clear();
        }
    }

    @Override
    public boolean exist(String key, T id) {
        ensureCacheData(key);
        Long rank = remoteCache.zrank(remoteKey(key), id);
        return rank != null && rank >= 0;
    }

    @Override
    public Map<String, Boolean> exist(Collection<String> keys, T id) {
        for (String key : keys) {
            ensureCacheData(key);
        }
        Map<String, String> remoteKeyKeyMap = keys.stream().collect(Collectors.toMap(n -> remoteKey(n), n -> n));
        Map<String, Long> rankMap = remoteCache.zrank(keys.stream()
                .map(k -> remoteKey(k))
                .collect(Collectors.toList()), id);
        return rankMap.entrySet().stream().collect(
                Collectors.toMap(
                        n -> remoteKeyKeyMap.get(n.getKey()),
                        n -> n.getValue() != null,
                        (k1, k2) -> k1));
    }

    @Override
    public List<T> paging(String key, Long score, Toward toward, int pageSize) {
        Double doubleScore = null;
        if (score != null) {
            doubleScore = score.doubleValue();
        }
        return paging(key, doubleScore, toward, pageSize);
    }

    @Override
    public List<T> paging(String key, Double score, Toward toward, int pageSize) {
        OffsetCacheKey cacheKey = new OffsetCacheKey(key, score, null, toward, pageSize);
        List<T> result = pagingExe.get(
                namespace, cacheKey,
                remoteKey -> {
                    Supplier<List<T>> cacheGetter;
                    if (cacheKey.toward == Toward.greater) {
                        cacheGetter = () -> {
                            List<T> list = remoteCache.zrangeByScore(remoteKey, cacheKey.getMin(), max_value, 0, cacheKey.pageSize);
                            Collections.reverse(list);
                            return list;
                        };
                    } else {
                        cacheGetter = () -> remoteCache.zrevrangeByScore(remoteKey, cacheKey.getMax(), min_value, 0, cacheKey.pageSize);
                    }
                    return cacheGetter.get();
                });
        return result;

    }


    @Override
    public List<T> paging(String key, Double maxScore, Double minScore, Toward toward, int pageSize) {
        OffsetCacheKey cacheKey = new OffsetCacheKey(key, maxScore, minScore, toward, pageSize);
        List<T> result = pagingExe.get(
                namespace, cacheKey,
                remoteKey -> {
                    Supplier<List<T>> cacheGetter;
                    if (cacheKey.toward == Toward.greater) {
                        cacheGetter = () -> {
                            List<T> list = remoteCache.zrangeByScore(remoteKey, cacheKey.getMin(), cacheKey.getMax(), 0, cacheKey.pageSize);
                            Collections.reverse(list);
                            return list;
                        };
                    } else {
                        cacheGetter = () -> remoteCache.zrevrangeByScore(remoteKey, cacheKey.getMax(), cacheKey.getMin(), 0, cacheKey.pageSize);
                    }
                    return cacheGetter.get();
                });
        return result;

    }

    @Override
    public List<T> paging(String key, Integer pageNum, Integer pageSize, Toward toward) {
        long start = (pageNum - 1) * pageSize;
        long stop = pageNum * pageSize - 1;
        PageCacheKey cacheKey = new PageCacheKey(key, start, stop, toward);
        List<T> result = pagingExe.get(
                namespace, cacheKey,
                remoteKey -> {
                    Supplier<List<T>> cacheGetter;
                    if (cacheKey.toward == Toward.greater) {
                        cacheGetter = () -> {
                            /**
                             * (分数从小到大)
                             */
                            List<T> list = remoteCache.zrange(remoteKey, start, stop);
                            Collections.reverse(list);
                            return list;
                        };
                    } else {
                        /**
                         * (分数从大到小)
                         */
                        cacheGetter = () -> remoteCache.zrevrange(remoteKey, start, stop);
                    }
                    return cacheGetter.get();
                });
        return result;
    }

    @Override
    public Map<T, Double> pagingWithScore(String key, Long score, Toward toward, int pageSize) {
        Double doubleScore = null;
        if (score != null) {
            doubleScore = score.doubleValue();
        }
        return pagingWithScore(key, doubleScore, toward, pageSize);
    }

    @Override
    public Map<T, Double> pagingWithScore(String key, Double score, Toward toward, int pageSize) {
        OffsetCacheKey cacheKey = new OffsetCacheKey(key, score, null, toward, pageSize);
        return pagingExe.get(
                namespace, cacheKey,
                remoteKey -> {
                    Supplier<Map<T, Double>> cacheGetter;
                    if (cacheKey.toward == Toward.greater) {
                        cacheGetter = () -> remoteCache.zrangeByScoreWithScore(remoteKey, cacheKey.getMin(), max_value, 0, cacheKey.pageSize);
                    } else {
                        cacheGetter = () -> remoteCache.zrevrangeByScoreWithScore(remoteKey, cacheKey.getMax(), min_value, 0, cacheKey.pageSize);
                    }
                    return cacheGetter.get();
                });
    }

    @Override
    public void del(String key) {
        remoteCache.del(remoteKey(key));
        if (localCache != null) {
            localCache.clear();
        }
    }

    @Override
    public Double score(String key, T id) {
        return score(key, Arrays.asList(id)).get(id);
    }

    @Override
    public Map<T, Double> score(String key, List<T> idList) {
        ensureCacheData(key);
        return remoteCache.zScore(remoteKey(key), idList);
    }

    @Override
    public void reload(String key) {
        String newRemoteKey = remoteReloadKey(key);
        loadData(key, newRemoteKey, loader);
    }

    @Override
    public void afterBuild() {
        pagingExe = new PagingExecutor(loader);

        if (localCache != null) {
            LocalCacheInterceptor interceptor = new LocalCacheInterceptor(localCache);
            pagingExe.addInterceptor(interceptor);
        }
        pagingExe.addInterceptor(new ReloadCacheInterceptor());

        pagingExe.addInterceptor(new NullValueInterceptor(namespace, nullValueExpireSecond));

        if (outOfRangeGetter != null) {
            pagingExe.addInterceptor(new OutOfRangeMapGetterInterceptor(outOfRangeGetter));
        }

    }

    private String remoteReloadKey(String key) {
        return remoteKey(key) + "$$reload";
    }

    class ReloadCacheInterceptor implements CacheInterceptor<CacheKey, Object> {

        @Override
        public boolean beforeRemoteGet(CacheGetContext<CacheKey, Object> context) {
            for (CacheKey cacheKey : context.getMissCacheKeys()) {
                String remoteReloadKey = remoteReloadKey(cacheKey.key);
                Map<T, Double> dataMap = remoteCache.zrangeWithScore(remoteReloadKey, 0, -1);
                if (CollectionUtils.isEmpty(dataMap)) {
                    continue;
                }
                String remoteKey = remoteKey(cacheKey.key);
                remoteCache.del(remoteKey);
                addAll(remoteKey, dataMap);
                remoteCache.del(remoteReloadKey);
            }
            return true;
        }
    }

    class OutOfRangeMapGetterInterceptor implements CacheInterceptor<CacheKey, Object> {

        OutOfRangeGetter<T> outOfRangeGetter;

        public OutOfRangeMapGetterInterceptor(OutOfRangeGetter<T> outOfRangeGetter) {
            this.outOfRangeGetter = outOfRangeGetter;
        }

        @Override
        public void beforeRebuild(CacheGetContext<CacheKey, Object> context, Map<String, Object> remoteKeyValueMap) {
            for (CacheKey _cacheKey : context.getMissCacheKeys()) {
                if (!(_cacheKey instanceof OffsetCacheKey)) {
                    continue;
                }
                OffsetCacheKey cacheKey = (OffsetCacheKey) _cacheKey;
                Object value = remoteKeyValueMap.get(remoteKey(cacheKey.key));
                if (value != null && CollectionUtils.size(value) == cacheKey.pageSize) {
                    continue;
                }
                if (!remoteCache.exist(remoteKey(cacheKey.key))) {
                    continue;
                }
                log.info("sorted cache get out of range [key={},maxScore={},minScore={},toward={},pageSize={}]",
                        cacheKey.key, cacheKey.maxScore,cacheKey.minScore, cacheKey.toward, cacheKey.pageSize);
                Map<T, Double> outOfRangeDataMap = outOfRangeGetter.get(cacheKey.key, cacheKey.maxScore,
                        cacheKey.minScore, cacheKey.toward, cacheKey.pageSize);
                if (List.class.isAssignableFrom(value.getClass())) {
                    value = outOfRangeDataMap
                            .entrySet().stream()
                            .sorted((n1, n2) -> n2.getValue().compareTo(n1.getValue()))
                            .map(n -> n.getKey()).collect(Collectors.toList());
                }
                context.setCacheValue(cacheKey, value);
            }
        }
    }

    static abstract class CacheKey {
        protected String key;

        public CacheKey(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    static class PageCacheKey extends CacheKey {
        private Long start;
        private Long stop;
        private Toward toward;

        public PageCacheKey(String key, Long start, Long stop, Toward toward) {
            super(key);
            this.start = start;
            this.stop = stop;
            this.toward = toward;
        }
    }

    static class OffsetCacheKey extends CacheKey {
        Double maxScore;
        Double minScore;
        Toward toward;
        int pageSize;


        public OffsetCacheKey(String key, Double maxScore, Double minScore, Toward toward, int pageSize) {
            super(key);
            this.maxScore = maxScore;
            this.minScore = minScore;
            this.toward = toward;
            this.pageSize = pageSize;
        }

        public String getMax() {
            return maxScore == null ? max_value : "(" + maxScore;
        }

        public String getMin() {
            return minScore == null ? min_value : "(" + minScore;
        }

    }

    class PagingExecutor<T> extends BaseCacheExecutor<CacheKey, SortedSetCache.Loader<T>> {

        public PagingExecutor(SortedSetCache.Loader<T> loader) {
            super(loader);
        }

        @Override
        protected void rebuildRemoteCache(Set<CacheKey> missKeys, SortedSetCache.Loader<T> loader) {
            for (CacheKey cacheKey : missKeys) {
                ensureCacheData(cacheKey.key, loader);
            }
        }

        @Override
        protected String remoteKey(CacheKey key) {
            return SortedSetCacheImpl.this.remoteKey(key.key);
        }
    }
}
