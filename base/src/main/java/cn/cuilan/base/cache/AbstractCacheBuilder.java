package cn.cuilan.base.cache;

import java.util.concurrent.TimeUnit;

public abstract class AbstractCacheBuilder<S extends AbstractCacheBuilder, LOADER extends AbstractCache.Loader> {

    /**
     * 默认本地缓存过期时间为10秒
     */
    private static final int DEFAULT_LOCAL_TTL = (int) TimeUnit.SECONDS.convert(10, TimeUnit.SECONDS);

    /**
     * 默认过期时间为1天
     */
    private static final int DEFAULT_REMOTE_TTL = (int) TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

    /**
     * 空数据默认过期时间为1分钟
     */
    private static final int DEFAULT_NULL_OBJECT_TTL = (int) TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES);

    protected String namespace;
    protected int remoteExpireSeconds = DEFAULT_REMOTE_TTL;
    protected int localExpireSeconds = DEFAULT_LOCAL_TTL;
    protected Integer localMaxKeySize;
    protected LOADER loader;
    protected int nullValueExpireSecond = DEFAULT_NULL_OBJECT_TTL;

    public AbstractCacheBuilder(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 设置空数据过期时间
     *
     * @param expire   时间
     * @param timeUnit 单位
     */
    @SuppressWarnings("unchecked")
    public S nullValueExpire(int expire, TimeUnit timeUnit) {
        nullValueExpireSecond = (int) TimeUnit.SECONDS.convert(expire, timeUnit);
        return (S) this;
    }

    /**
     * 设置key的过期时间，默认为1天
     *
     * @param expire   时间
     * @param timeUnit 单位
     */
    @SuppressWarnings("unchecked")
    public S remoteExpire(int expire, TimeUnit timeUnit) {
        remoteExpireSeconds = (int) TimeUnit.SECONDS.convert(expire, timeUnit);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S loader(LOADER loader) {
        this.loader = loader;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S local(int localCapacity, int localExpire, TimeUnit unit) {
        localExpireSeconds = (int) TimeUnit.SECONDS.convert(localExpire, unit);
        localMaxKeySize = localCapacity;
        return (S) this;
    }

    /**
     * 公共构建缓存
     */
    @SuppressWarnings("unchecked")
    protected void commonBuild(AbstractCache cache) {
        if (Caches.cacheMap.containsKey(namespace)) {
            throw new RuntimeException(String.format("cache.namespace already exists[namespace=%s]", namespace));
        }
        Caches.cacheMap.put(namespace, cache);
        cache.setNamespace(namespace);
        cache.setLoader(loader);
        // 设置过期时间
        if (remoteExpireSeconds > 0) {
            cache.setRemoteExpireSecond(remoteExpireSeconds);
        }
        if (localMaxKeySize != null) {
            LocalCache localCache = new LocalCache(namespace, localExpireSeconds, localMaxKeySize);
            cache.setLocalCache(localCache);
            Caches.localCacheMap.put(namespace, localCache);
        }
        // 设置空数据过期时间
        cache.setNullValueExpireSecond(nullValueExpireSecond);
        cache.selfAfterBuild();
    }

    public abstract Cache build();
}
