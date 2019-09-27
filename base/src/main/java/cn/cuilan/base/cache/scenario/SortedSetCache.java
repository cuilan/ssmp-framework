package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.Cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 可排序缓存
 *
 * @param <T>
 */
public interface SortedSetCache<T> extends Cache<String> {

    void add(String key, T id, double score);

    void addAllByMap(String key, Map<T, Double> map);

    long size(String key);

    void remove(String key, T id);

    boolean exist(String key, T id);

    Map<String, Boolean> exist(Collection<String> key, T id);

    List<T> paging(String key, Long score, Toward toward, int pageSize);

    List<T> paging(String key, Double score, Toward toward, int pageSize);

    List<T> paging(String key, Double startScore, Double endScore, Toward toward, int pageSize);

    List<T> paging(String key, Integer pageNum, Integer pageSize, Toward toward);

    Map<T, Double> pagingWithScore(String key, Long score, Toward toward, int pageSize);

    Map<T, Double> pagingWithScore(String key, Double score, Toward toward, int pageSize);

    void del(String key);

    Double score(String key, T id);

    Map<T, Double> score(String key, List<T> idList);

    void reload(String key);

    interface Loader<T> extends AbstractCache.Loader<String, Map<T, Double>> {
        /**
         * 根据传入的 key返回有序集合
         *
         * @param key
         * @return 有序集合，返回格式 <元素,元素的排序分值>
         */
        @Override
        Map<T, Double> load(String key);
    }

    interface OutOfRangeGetter<T> {
        Map<T, Double> get(String key, Double maxScore, Double minScore, Toward toward, int pageSize);
    }
}
