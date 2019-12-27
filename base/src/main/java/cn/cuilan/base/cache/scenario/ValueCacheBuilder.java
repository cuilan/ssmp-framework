package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCacheBuilder;

public class ValueCacheBuilder<K, V> extends AbstractCacheBuilder<ValueCacheBuilder<K, V>, ValueCache.Loader<K, V>> {

    public ValueCacheBuilder(String namespace, Class<K> keyClass, Class<V> valueClass) {
        super(namespace);
    }

    @Override
    public ValueCache<K, V> build() {
        ValueCache cache = new ValueCache();
        commonBuild(cache);
        return cache;
    }


}
