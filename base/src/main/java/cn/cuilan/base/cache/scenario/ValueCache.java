package cn.cuilan.base.cache.scenario;

import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.core.BaseCacheExecutor;
import cn.cuilan.base.cache.interceptor.LocalCacheInterceptor;
import cn.cuilan.base.cache.interceptor.NullValueInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ValueCache<K, V> extends AbstractCache<K, ValueCache.Loader<K, V>> {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    CacheExecutor cacheExecutor;

    public V get(K key, Loader<K, V> loader) {
        return cacheExecutor.get(
                namespace, key,
                remoteKey -> remoteCache.get(remoteKey), loader);
    }

    public V get(K key) {
        return get(key, loader);
    }

    public Map<K, V> getAll(Collection<K> keys) {
        return cacheExecutor.getBulk(
                namespace, keys,
                remoteKeys -> remoteCache.mget(remoteKeys));
    }

    public Map<K, V> getAll(Collection<K> keys, Loader<K, V> loader) {
        return cacheExecutor.getBulk(
                namespace, keys,
                remoteKeys -> remoteCache.mget(remoteKeys), loader);
    }

    public void set(K key, V value) {
        if (value == null) {
            return;
        }
        update(key, rawKey ->
                remoteCache.set(rawKey, value, remoteExpireSecond, TimeUnit.SECONDS)
        );
    }

    public boolean setIfAbsent(K key, V value) {
        if (value == null) {
            return false;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        update(key, rawKey -> result.set(remoteCache.setNX(rawKey, value, remoteExpireSecond, TimeUnit.SECONDS))
        );
        return result.get();
    }

    protected void update(K key, Consumer<String> consumer) {
        String rawKey = remoteKey(key);
        boolean exist = remoteCache.exist(rawKey);
        //如果远程缓存不存在，则不更新
        if (loader != null && !exist) {
            return;
        }
        consumer.accept(rawKey);
        remoteCache.expire(rawKey, remoteExpireSecond, TimeUnit.SECONDS);
        nullValueInterceptor.delete(key);
        if (localCache != null) {
            localCache.del(key);
        }
    }

    public void del(Collection<K> keys) {
        remoteCache.del(keys.stream().map(key -> remoteKey(key)).collect(Collectors.toList()));
        for (K k : keys) {
            nullValueInterceptor.delete(k);
        }
        if (localCache != null) {
            localCache.del(keys);
        }
    }

    @Override
    public void del(K key) {
        del(Arrays.asList(key));
    }

    NullValueInterceptor nullValueInterceptor;

    @Override
    public void afterBuild() {
        cacheExecutor = new CacheExecutor(loader);
        if (localCache != null) {
            cacheExecutor.addInterceptor(new LocalCacheInterceptor(localCache));
        }
        nullValueInterceptor = new NullValueInterceptor<>(namespace, nullValueExpireSecond);
        cacheExecutor.addInterceptor(nullValueInterceptor);
    }


    public interface Loader<K, V> extends AbstractCache.Loader<Set<K>, Map<K, V>> {
        @Override
        Map<K, V> load(Set<K> ks);

    }

    class CacheExecutor extends BaseCacheExecutor<K, Loader<K, V>> {

        public CacheExecutor(Loader<K, V> loader) {
            super(loader);
        }

        @Override
        protected void rebuildRemoteCache(Set<K> missKeys, Loader<K, V> loader) {
            Map<String, V> remoteKVMap = loader.load(missKeys).entrySet().stream()
                    .collect(Collectors.toMap(entry -> remoteKey(entry.getKey()), entry -> entry.getValue()));
            remoteCache.mset(remoteKVMap, remoteExpireSecond);
        }


        @Override
        protected String remoteKey(K key) {
            return ValueCache.this.remoteKey(key);
        }
    }
}
