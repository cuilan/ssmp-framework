package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCacheBuilder;

public class PageCacheBuilder<T> extends AbstractCacheBuilder<PageCacheBuilder<T>, PageCache.Loader<T>> {
    public PageCacheBuilder(String namespace) {
        super(namespace);
    }

    @Override
    public PageCache<T> build() {
        PageCache cache = new PageCache();
        commonBuild(cache);
        return cache;
    }
}
