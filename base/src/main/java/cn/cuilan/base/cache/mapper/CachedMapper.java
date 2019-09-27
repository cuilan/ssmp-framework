package cn.cuilan.base.cache.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import cn.cuilan.base.dataObserver.ObserverContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CachedMapper<T> extends BaseMapper<T> {
    String id = "id";
    String select = "<script>select ";
    String selectEnd = "</script>";
    String from = " from ";
    String where1 = " where 1=1 ";
    String a = ",";
    String left_join = " left join ";
    String on = " on ";
    /**
     * &lt;	<	小于
     */
    String LT = " &lt; ";
    /**
     * &gt;	>	大于
     */
    String GT = " &gt; ";

    /**
     * 通过id获取实体List，优先从缓存中获取
     *
     * @param ids
     * @return
     */
    List<T> selectBatchIdsCached(Collection<Long> ids);

    /**
     * 通过id获取实体Map<ID,实体>,优先从缓存中获取
     *
     * @return
     */
    Map<Long, T> selectBatchIdsMapCached(Collection<Long> ids);

    T selectByIdCached(Long id);

    void evictCache(Long id);

    // TODO
//    int insert(T entity, ObserverContext context);
}
