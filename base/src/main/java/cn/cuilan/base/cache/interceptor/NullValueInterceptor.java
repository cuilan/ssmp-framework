package cn.cuilan.base.cache.interceptor;

import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.Caches;
import cn.cuilan.base.cache.core.CacheGetContext;
import cn.cuilan.base.cache.core.CacheInterceptor;
import cn.cuilan.base.cache.event.CacheEventEnum;
import cn.cuilan.base.cache.utils.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class NullValueInterceptor<K, V> implements CacheInterceptor<K, V> {
    static String NULL_VALUE = "_NULL_";
    String namespace;
    int nullValueExcpireSecond;

    public NullValueInterceptor(String namespace, int nullValueExcpireSecond) {
        this.namespace = namespace;
        this.nullValueExcpireSecond = nullValueExcpireSecond;
    }

    private String getNullValueKey(K k) {
        String kStr = "null";
        if (k != null) {
            kStr = k.toString();
        }
        return AbstractCache.remoteCachePrefix + ":NULL_VAL:" + namespace + ":" + kStr;
    }

    @Override
    public void beforeRebuild(CacheGetContext<K, V> context, Map<String, V> remoteKeyValueMap) {
        if (CollectionUtils.isEmpty(context.getMissCacheKeys())) {
            return;
        }
        int requestCount = context.getMissCacheKeys().size();
        Map<String, K> nullKeyMap = new HashMap<>();
        List<String> nullValueKeys = context.getMissCacheKeys().stream()
                .map(k -> {
                    String nullValueKey = getNullValueKey(k);
                    nullKeyMap.put(nullValueKey, k);
                    return nullValueKey;
                }).collect(Collectors.toList());

        Map<String, V> nullValMap = AbstractCache.remoteCache.mget(nullValueKeys);
        for (K k : new HashSet<>(context.getMissCacheKeys())) {
            String nullValKey = getNullValueKey(k);
            if (NULL_VALUE.equals(nullValMap.get(nullValKey))) {
                context.getMissCacheKeys().remove(k);
            }
        }
        Caches.collectEvent(
                context.getNamespace(),
                CacheEventEnum.NULL_HIT,
                requestCount - context.getMissCacheKeys().size());

        context.setInterceptorValue(this, context.getMissCacheKeys());
    }

    @Override
    public void afterRemoteGet(CacheGetContext<K, V> context) {
        Set<K> missKeySet = context.getInterceptorValue(this);
        if (CollectionUtils.isEmpty(missKeySet)) {
            return;
        }
        Map<String, String> missRemoteKeyMap = new HashMap<>();
        for (K k : missKeySet) {
            if (context.getMissCacheKeys().contains(k)) {
                missRemoteKeyMap.put(getNullValueKey(k), NULL_VALUE);
            }
        }
        if (!missRemoteKeyMap.isEmpty()) {
            AbstractCache.remoteCache.mset(missRemoteKeyMap, nullValueExcpireSecond);
        }
    }

    public void delete(K k) {
        AbstractCache.remoteCache.del(getNullValueKey(k));
    }
}
