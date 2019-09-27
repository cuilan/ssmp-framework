package cn.cuilan.base.cache.core;

import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.Caches;
import cn.cuilan.base.cache.event.CacheEventEnum;
import cn.cuilan.base.cache.utils.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseCacheExecutor<K, Loader extends AbstractCache.Loader> {

    List<CacheInterceptor> cacheInterceptors = new ArrayList<>();

    Loader loader;

    public BaseCacheExecutor(Loader loader) {
        this.loader = loader;
    }

    public void addInterceptor(CacheInterceptor<K, Object> cacheInterceptor) {
        cacheInterceptors.add(cacheInterceptor);
    }

    public <V> V get(String namespace, K key, RemoteCacheGetter<V> cacheGetter) {
        return get(namespace, key, cacheGetter, loader);
    }

    public <V> V get(String namespace, K key, RemoteCacheGetter<V> cacheGetter, Loader loader) {
        return getBulk(
                namespace, Collections.singletonList(key),
                keys -> {
                    Map<String, V> result = new HashMap<>();
                    for (String remoteKey : keys) {
                        result.put(remoteKey, cacheGetter.get(remoteKey));
                    }
                    return result;
                },
                loader).get(key);
    }

    public <V> Map<K, V> getBulk(String namespace, Collection<K> keys, RemoteCacheBulkGetter<V> cacheGetter) {
        return getBulk(namespace, keys, cacheGetter, loader);
    }

    public <V> Map<K, V> getBulk(String namespace, Collection<K> keys, RemoteCacheBulkGetter<V> cacheGetter, Loader newLoader) {
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.EMPTY_MAP;
        }
        CacheGetContext<K, V> context = new CacheGetContext<>(namespace, keys, cacheGetter);

        if (newLoader != null) {
            context.setCacheLoader(newLoader);
        } else {
            context.setCacheLoader(loader);
        }

        return cacheGet(context);
    }

    @SuppressWarnings("unchecked")
    private <V> Map<K, V> cacheGet(CacheGetContext<K, V> context) {
        int currentInterceptorIndex = -1;
        boolean isContinue = true;
        for (CacheInterceptor value : cacheInterceptors) {
            if (!value.beforeRemoteGet(context)) {
                isContinue = false;
                break;
            }
            currentInterceptorIndex++;
        }
        if (isContinue) {
            Map<String, V> remoteKeyValueMap = remoteGet(context);
            if (context.getCacheLoader() != null && !CollectionUtils.isEmpty(context.getMissCacheKeys())) {
                for (CacheInterceptor interceptor : cacheInterceptors) {
                    interceptor.beforeRebuild(context, remoteKeyValueMap);
                }
                if (!CollectionUtils.isEmpty(context.getMissCacheKeys())) {
                    long start = System.currentTimeMillis();
                    rebuildRemoteCache(context.getMissCacheKeys(), (Loader) context.getCacheLoader());
                    long duration = System.currentTimeMillis() - start;
                    Caches.collectEvent(context.getNamespace(), CacheEventEnum.DB_LOAD, context.getMissCacheKeys().size());
                    Caches.collectEvent(context.getNamespace(), CacheEventEnum.DB_TIME, duration);
                    for (CacheInterceptor cacheInterceptor : cacheInterceptors) {
                        cacheInterceptor.afterRebuild(context);
                    }
                    remoteGet(context);
                }
            }
        }


        for (int i = currentInterceptorIndex; i >= 0; i--) {
            cacheInterceptors.get(i).afterRemoteGet(context);
        }
        return context.getKeyValueMap();
    }


    @SuppressWarnings("unchecked")
    private <V> Map<String, V> remoteGet(CacheGetContext<K, V> context) {
        Caches.collectEvent(context.getNamespace(), CacheEventEnum.REMOTE_REQ, context.getMissCacheKeys().size());
        Map<String, K> remoteKeyMap = new HashMap<>();
        Map<K, String> keyRemoteMap = new HashMap<>();
        context.getMissCacheKeys().forEach(k -> {
            String remoteKey = remoteKey(k);
            remoteKeyMap.put(remoteKey, k);
            keyRemoteMap.put(k, remoteKey);
        });
        Map<String, V> remoteKeyValueMap = context.getRemoteGetter().get(keyRemoteMap.values());
        AtomicInteger hitCount = new AtomicInteger();
        remoteKeyValueMap.forEach((k, v) -> {
            if (!CollectionUtils.isEmpty(v)) {
                hitCount.getAndIncrement();
            }
            context.setCacheValue(remoteKeyMap.get(k), v);
        });
        Caches.collectEvent(context.getNamespace(), CacheEventEnum.REMOTE_HIT, hitCount.get());
        return remoteKeyValueMap;
    }

    protected abstract String remoteKey(K key);

    protected abstract void rebuildRemoteCache(Set<K> missKeys, Loader loader);

    public interface RemoteCacheBulkGetter<V> {
        Map<String, V> get(Collection<String> remoteKeys);
    }

    public interface RemoteCacheGetter<V> {
        V get(String key);
    }
}
