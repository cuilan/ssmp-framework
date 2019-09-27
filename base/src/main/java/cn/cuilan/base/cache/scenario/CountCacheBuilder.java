package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCacheBuilder;

public class CountCacheBuilder extends AbstractCacheBuilder<CountCacheBuilder, ValueCache.Loader<String, Integer>> {

    public CountCacheBuilder(String namespace) {
        super("count:" + namespace);
    }

    @Override
    public CountCache build() {
        CountCache cache = new CountCache();
        commonBuild(cache);
        return cache;
    }


}
