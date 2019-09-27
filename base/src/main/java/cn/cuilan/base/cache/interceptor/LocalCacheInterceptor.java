package cn.cuilan.base.cache.interceptor;

import cn.cuilan.base.cache.Caches;
import cn.cuilan.base.cache.LocalCache;
import cn.cuilan.base.cache.core.CacheGetContext;
import cn.cuilan.base.cache.core.CacheInterceptor;
import cn.cuilan.base.cache.event.CacheEventEnum;
import cn.cuilan.base.cache.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class LocalCacheInterceptor<K, V> implements CacheInterceptor<K, V> {

    LocalCache<K, V> localCache;

    public LocalCacheInterceptor(LocalCache localCache) {
        this.localCache = localCache;
    }

    @Override
    public boolean beforeRemoteGet(CacheGetContext<K, V> context) {
        Set<K> localMissKey = new HashSet<>(context.getMissCacheKeys());
        int localRequestCount = context.getMissCacheKeys().size();
        Caches.collectEvent(context.getNamespace(), CacheEventEnum.LOCAL_REQ, localRequestCount);
        Map<K, V> localKVMap = localCache.getAllPresent(context.getMissCacheKeys());
        localKVMap.forEach((k, v) -> {
            V value = localKVMap.get(k);
            if (value != null) {
                context.setCacheValue(k, value);
                localMissKey.remove(k);
            }
        });
        Caches.collectEvent(context.getNamespace(), CacheEventEnum.LOCAL_HIT, localRequestCount - localMissKey.size());
        if (localMissKey.isEmpty()) {
            return false;
        }
        context.setInterceptorValue(this, localMissKey);
        return true;
    }

    @Override
    public void afterRemoteGet(CacheGetContext<K, V> context) {
        Set<K> localMissKey = context.getInterceptorValue(this);
        if (CollectionUtils.isEmpty(localMissKey)) {
            return;
        }
        for (K k : localMissKey) {
            V v = context.getCacheValue(k);
            if (v != null) {
//                log.info("[{}] Cache is already a proxy class, which is set up twice.", ClassUtils.isCglibProxy(v));
                localCache.set(k, v);
            }
        }
    }

}
