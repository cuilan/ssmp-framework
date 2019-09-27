package cn.cuilan.base.cache.utils;

import java.util.*;

/**
 * 自定义集合工具类，适合对redis数据结构做统一判断处理
 *
 * @author zhang.yan
 */
public class CollectionUtils {

    /**
     * 判断Map是否为空，空集也视为空
     */
    public static boolean isEmpty(Map map) {
        if (map == null) {
            return true;
        }
        return map.size() == 0;
    }

    /**
     * 判断Collection是否为空，空集也视为空
     */
    public static boolean isEmpty(Collection collection) {
        if (collection == null) {
            return true;
        }
        return collection.size() == 0;
    }

    /**
     * 判断给定任意集合（单列或双列）是否为空，空集也视为kong
     */
    public static boolean isEmpty(Object result) {
        if (result instanceof Collection) {
            return isEmpty(result);
        }
        if (result instanceof Map) {
            return isEmpty(result);
        }
        return result == null;
    }

    public static int size(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Map) {
            return ((Map) value).size();
        }
        if (value instanceof Collection) {
            return ((Collection) value).size();
        }
        return 0;
    }

    public static <T> List randomFromCollection(List<T> list, Integer n) {
        if (null == n || n <= 0 || CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        if (list.size() < n) {
            n = list.size();
        }
        List<T> result = new ArrayList<>();
        Random random = new Random();
        int size = list.size();
        while (result.size() < n) {
            int nextInt = random.nextInt(size);
            T item = list.get(nextInt);
            if (result.contains(item)) {
                continue;
            }
            result.add(item);
        }
        return result;
    }

    public static <T> List<T> singleList(T t) {
        List<T> list = new ArrayList<>(1);
        list.add(t);
        return list;
    }

    public static List emptyList() {
        return new ArrayList<>(0);
    }
}
