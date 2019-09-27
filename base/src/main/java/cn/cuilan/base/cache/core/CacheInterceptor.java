package cn.cuilan.base.cache.core;

import java.util.Map;

/**
 * 缓存拦截器
 *
 * @param <K>
 * @param <R>
 * @author zhang.yan
 */
public interface CacheInterceptor<K, R> {

    default boolean beforeRemoteGet(CacheGetContext<K, R> context) {
        return true;
    }

    default void afterRemoteGet(CacheGetContext<K, R> context) {
    }

    default void beforeRebuild(CacheGetContext<K, R> context, Map<String, R> remoteKeyValueMap) {
    }

    default void afterRebuild(CacheGetContext<K, R> context) {
    }

    default void beforeUpdate() {
    }

    default void afterUpdate() {
    }

}
