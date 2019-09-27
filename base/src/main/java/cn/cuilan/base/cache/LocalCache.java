package cn.cuilan.base.cache;

import cn.cuilan.base.cache.utils.CollectionUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class LocalCache<K, V> {

    static LocalCacheSynchronizer localCacheSynchronizer;
    private Cache<String, V> cache;
    private String namespace;

    /**
     * 构造方法
     *
     * @param namespace          命名空间
     * @param expireMilliSeconds 过期时间，单位：秒
     * @param maxSize            最大size
     */
    public LocalCache(String namespace, long expireMilliSeconds, Integer maxSize) {
        this.namespace = namespace;
        cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .softValues()
                .expireAfterWrite(expireMilliSeconds, TimeUnit.SECONDS)
                .expireAfterAccess(expireMilliSeconds, TimeUnit.SECONDS)
                .build();
    }

    private Enhancer getDelegateFactory(V v) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(v.getClass());
        enhancer.setCallback(new CglibCallback(v));
        return enhancer;
    }

    private String keyStr(K k) {
        return String.valueOf(k);
    }

    private boolean isBoxedPrimitive(Object v) {
        if (v == null) {
            return false;
        }
        if (v.getClass().isAssignableFrom(Integer.class)) {
            return true;
        }
        if (v.getClass().isAssignableFrom(Short.class)) {
            return true;
        }
        if (v.getClass().isAssignableFrom(Long.class)) {
            return true;
        }
        if (v.getClass().isAssignableFrom(Float.class)) {
            return true;
        }
        if (v.getClass().isAssignableFrom(Double.class)) {
            return true;
        }
        if (v.getClass().isAssignableFrom(Character.class)) {
            return true;
        }
        if (v.getClass().isAssignableFrom(Byte.class)) {
            return true;
        }
        if (v.getClass().isAssignableFrom(Byte.class)) {
            return true;
        }
        return v.getClass().getName().equals(String.class.getName());
    }

    @SuppressWarnings("unchecked")
    private V delegate(V v) {
        if (v == null) {
            return null;
        }
        if (isBoxedPrimitive(v)) {
            return v;
        }
        if (ClassUtils.isCglibProxy(v)) {
            return v;
        }
        return (V) getDelegateFactory(v).create();
    }

    /**
     * 获取全部，如果存在
     *
     * @param ids id集合
     * @return map
     */
    public Map<K, V> getAllPresent(Collection<K> ids) {
        Map<K, V> newMap = new HashMap<>();
        ids.forEach(k -> {
            V v = cache.getIfPresent(keyStr(k));
            if (v == null) {
                return;
            }
            newMap.put(k, v);
        });
        return newMap;
    }

    /**
     * 获取如果存在
     *
     * @param key key
     * @return value
     */
    public V getIfPresent(K key) {
        return cache.getIfPresent(keyStr(key));
    }

    /**
     * 设置
     *
     * @param key        key
     * @param cacheValue value
     */
    public void set(K key, V cacheValue) {
        cache.put(keyStr(key), delegate(cacheValue));
    }

    /**
     * 删除
     *
     * @param key key
     */
    public void del(K key) {
        del(Collections.singletonList(key));
    }

    /**
     * 清空
     */
    public void clear() {
        cache.invalidateAll();
        if (localCacheSynchronizer != null) {
            localCacheSynchronizer.synchronize(this.namespace);
        }
    }

    /**
     * 删除多个
     */
    public void del(Collection<K> keys) {
        Collection<String> keyStrs = keys.stream().filter(key -> key != null).map(key -> keyStr(key)).collect(Collectors.toList());
        cache.invalidateAll(keyStrs);
        if (localCacheSynchronizer != null) {
            localCacheSynchronizer.synchronize(this.namespace, keys);
        }
    }

    public void delNoSync(Collection<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            cache.invalidateAll();
            log.debug("同步本地缓存[namespace={},keys={}]", this.namespace, "所有");
        } else {
            cache.invalidateAll(keys.stream().filter(Objects::nonNull).map(k -> keyStr(k)).collect(Collectors.toList()));
            log.debug("同步本地缓存[namespace={},keys={}]", this.namespace, keys);
        }
    }

    private class CglibCallback implements MethodInterceptor {

        Object target;

        private CglibCallback(Object target) {
            this.target = target;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getName().startsWith("set")) {
                throw new RuntimeException(String.format("不允许修改缓存对象的字段值[class=%s]", target));
            }
            return methodProxy.invoke(target, args);
        }
    }
}
