package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCacheBuilder;

public class ListCacheBuilder<K, T> extends AbstractCacheBuilder<ListCacheBuilder<K,T>, ListCache.Loader<K, T>> {
    public ListCacheBuilder(String namespace) {
        super(namespace);
    }

    @Override
    public ListCache<K, T> build() {
        ListCache<K, T> cache = new ListCache<>();
        commonBuild(cache);
        return cache;
    }
}
