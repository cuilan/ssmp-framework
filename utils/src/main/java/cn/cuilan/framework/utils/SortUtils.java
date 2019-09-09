package cn.cuilan.framework.utils;


import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortUtils {
    public static <V, S extends Comparable> List<V> getSortedListFromMap(Map<V, S> map) {
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyList();
        }
        List<Map.Entry<V, S>> list = new ArrayList<>(map.entrySet());
        list.sort((entry, entry2) -> entry2.getValue().compareTo(entry.getValue()));
        return list.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
