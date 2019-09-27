package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCacheBuilder;

import java.util.concurrent.TimeUnit;

public class SortedSetCacheBuilder<T> extends AbstractCacheBuilder<SortedSetCacheBuilder<T>, SortedSetCache.Loader<T>> {

    Integer maxElements;
    SortedSetCache.OutOfRangeGetter<T> outOfRangeGetter;

    public SortedSetCacheBuilder(String namespace) {
        super(namespace);
    }

    /**
     * 集合中元素的最大数量
     * 如果超过此数量将自动淘汰分数最低的元素
     *
     * @param size
     * @return
     */
    public SortedSetCacheBuilder<T> maxElementsPerSet(int size) {
        this.maxElements = size;
        return this;
    }

    /**
     * 当score不在缓存范围内时的获取操作
     *
     * @return
     */
    public SortedSetCacheBuilder<T> outOfRangeGetter(SortedSetCache.OutOfRangeGetter<T> loader) {
        this.outOfRangeGetter = loader;
        return this;
    }

    /**
     * 本地可以缓存的页数
     * 每个不同参数的调用为一页
     *
     * @param localMaxPages 本地缓存的最大页数
     * @return
     */
    @Override
    public SortedSetCacheBuilder<T> local(int localMaxPages, int localExpire, TimeUnit timeUnit) {
        this.localMaxKeySize = localMaxPages;
        this.localExpireSeconds = (int)TimeUnit.SECONDS.convert(localExpire, timeUnit);
        return this;
    }


    @Override
    public SortedSetCache<T> build() {
        SortedSetCacheImpl<T> sortedCache = new SortedSetCacheImpl();
        sortedCache.setMaxElements(maxElements);
        sortedCache.setOutOfRangeGetter(outOfRangeGetter);
        commonBuild(sortedCache);
        return sortedCache;
    }




}
