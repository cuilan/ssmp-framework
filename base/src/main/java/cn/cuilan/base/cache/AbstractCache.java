package cn.cuilan.base.cache;

import org.apache.commons.lang3.StringUtils;

/**
 * 抽象缓存
 *
 * @param <K>      key
 * @param <LOADER>
 */
public abstract class AbstractCache<K, LOADER> implements Cache<K> {

    public static RemoteCache remoteCache;
    public static String remoteCachePrefix;
    protected int nullValueExpireSecond;
    protected Integer remoteExpireSecond;
    protected String namespace;
    protected LOADER loader;
    protected LocalCache localCache;

    public abstract void del(K key);

    protected void setLocalCache(LocalCache localCache) {
        this.localCache = localCache;
    }

    protected String getNamespace() {
        return namespace;
    }

    protected void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    protected void setRemoteExpireSecond(Integer remoteExpireSecond) {
        this.remoteExpireSecond = remoteExpireSecond;
    }

    protected void setLoader(LOADER loader) {
        this.loader = loader;
    }

    protected String remoteKey(K key) {
        StringBuilder builder = new StringBuilder().append(remoteCachePrefix).append(":").append(namespace);
        if (key != null && StringUtils.isNotBlank(key.toString())) {
            builder.append(":").append(key.toString());
        }
        return builder.toString();
    }

    public void setNullValueExpireSecond(int nullValueExpireSecond) {
        this.nullValueExpireSecond = nullValueExpireSecond;
    }

    final void selfAfterBuild() {
        afterBuild();
    }

    protected void afterBuild() {
    }

    public interface Loader<T, R> {
        R load(T t);
    }
}
