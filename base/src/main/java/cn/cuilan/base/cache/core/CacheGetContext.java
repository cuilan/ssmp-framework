package cn.cuilan.base.cache.core;

import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.utils.CollectionUtils;

import java.util.*;

public class CacheGetContext<K, V> {

    private String namespace;
    private Set<K> missCacheKeys;
    private Map<K, V> keyValueMap = new HashMap<>();
    private BaseCacheExecutor.RemoteCacheBulkGetter remoteGetter;
    private AbstractCache.Loader cacheLoader;
    private Map<CacheInterceptor, Object> interceptorValueMap = new HashMap<>();

    public CacheGetContext(String namespace, Collection<K> allCacheKey, BaseCacheExecutor.RemoteCacheBulkGetter<V> remoteGetter) {
        this.namespace = namespace;
        this.missCacheKeys = new HashSet<>(allCacheKey);
        this.remoteGetter = remoteGetter;
    }

    public Set<K> getMissCacheKeys() {
        return missCacheKeys;
    }

    public V getCacheValue(K k) {
        return keyValueMap.get(k);
    }

    public void setCacheValue(K key, V v) {
        keyValueMap.put(key, v);
        if (!CollectionUtils.isEmpty(v)) {
            missCacheKeys.remove(key);
        }
    }

    public Map<K, V> getKeyValueMap() {
        return keyValueMap;
    }

    public String getNamespace() {
        return namespace;
    }

    public AbstractCache.Loader getCacheLoader() {
        return cacheLoader;
    }

    public void setCacheLoader(AbstractCache.Loader cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    public void setInterceptorValue(CacheInterceptor interceptor, Object obj) {
        this.interceptorValueMap.put(interceptor, obj);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInterceptorValue(CacheInterceptor interceptor) {
        return (T) this.interceptorValueMap.get(interceptor);
    }

    public BaseCacheExecutor.RemoteCacheBulkGetter getRemoteGetter() {
        return remoteGetter;
    }
}
