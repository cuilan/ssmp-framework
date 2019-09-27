package cn.cuilan.base.cache.scenario;


import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.core.BaseCacheExecutor;
import cn.cuilan.base.cache.interceptor.LocalCacheInterceptor;
import cn.cuilan.base.cache.interceptor.NullValueInterceptor;
import cn.cuilan.base.cache.utils.CollectionUtils;
import cn.cuilan.base.cache.utils.RedisLock;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ListCache<K, T> extends AbstractCache<K, ListCache.Loader<K, T>> {

    CacheExecutor<List<T>> cacheExecutor;
    NullValueInterceptor nullValueInterceptor;
    @Override
    public void del(K key) {
        ensureCacheValue(key);
        remoteCache.del(remoteKey(key));
        if (localCache != null) {
            localCache.clear();
        }
    }

    private void ensureCacheValue(K key) {
        String remoteKey = remoteKey(key);
        if (loader != null && !remoteCache.exist(remoteKey)) {
            try (RedisLock.Lock lock = RedisLock.lock(remoteKey)) {
                if (remoteCache.exist(remoteKey)) {
                    return;
                }
                List data = loader.load(key);
                if (CollectionUtils.isEmpty(data)) {
                    return;
                }
                remoteCache.del(remoteKey);
                remoteCache.lpush(remoteKey, data);
                remoteCache.expire(remoteKey, remoteExpireSecond, TimeUnit.SECONDS);
            }
        }
    }


    public List<T> getAll(K key) {
        return range(key, 0, -1);
    }

    public List<T> range(K key, int start, int end) {
        return cacheExecutor.get(namespace, new CacheKey(key, start, end),
                remoteKey -> {
                    return remoteCache.lrange(remoteKey, start, end);
                });
    }

    public void addFirst(K key, T t) {
        ensureCacheValue(key);
        remoteCache.lpush(remoteKey(key), t);
    }

    public void addLast(K key, T t) {
        ensureCacheValue(key);
        remoteCache.rpush(remoteKey(key), t);
    }

    public T removeFirst(K key) {
        ensureCacheValue(key);
        return remoteCache.lpop(remoteKey(key));
    }

    public List<T> removeFirst(K key, int size) {
        ensureCacheValue(key);
        return remoteCache.lpop(remoteKey(key), size);
    }

    public T removeLast(K key) {
        ensureCacheValue(key);
        return remoteCache.rpop(remoteKey(key));
    }

    public long remove(K key, T t) {
        ensureCacheValue(key);
        return remoteCache.lrem(remoteKey(key), 0, t);
    }

    public long size(K key) {
        ensureCacheValue(key);
        return remoteCache.llen(remoteKey(key));
    }

    @Override
    public void afterBuild() {
        cacheExecutor = new CacheExecutor(loader);
        if (localCache != null) {
            cacheExecutor.addInterceptor(new LocalCacheInterceptor(localCache));
        }
        nullValueInterceptor = new NullValueInterceptor<>(namespace, 2);
        cacheExecutor.addInterceptor(nullValueInterceptor);
    }

    public interface Loader<K, T> extends AbstractCache.Loader<K, List<T>> {
    }

    class CacheExecutor<T> extends BaseCacheExecutor<CacheKey, Loader> {

        public CacheExecutor(Loader loader) {
            super(loader);
        }

        @Override
        protected void rebuildRemoteCache(Set<CacheKey> missKeys, Loader loader) {
            for (CacheKey cacheKey : missKeys) {
                ensureCacheValue(cacheKey.key);
            }
        }

        @Override
        protected String remoteKey(CacheKey key) {
            return ListCache.this.remoteKey(key.key);
        }
    }

//    class RemoteCacheInterceptor extends RemoteCollectionInterceptor<CacheKey, List<T>> {
//
//        @Override
//        protected Supplier<List<T>> remoteGetter(CacheKey cacheKey) {
//            return () -> remoteCache.lrange(cacheKey.remoteKey, cacheKey.start, cacheKey.end);
//        }
//
//    }

    class CacheKey {
        K key;
        int start;
        int end;
        String remoteKey;

        public CacheKey(K key, int start, int end) {
            this.key = key;
            this.start = start;
            this.end = end;
            this.remoteKey = remoteKey(key);
        }

        @Override
        public String toString() {
            return "CacheKey{" +
                    "key=" + key +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}
