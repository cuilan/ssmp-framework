package cn.cuilan.framework.utils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapResult extends AbstractMap<String, Object> {
    Map map = new HashMap();

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public MapResult data(String key, Object obj) {
        map.put(key, obj);
        return this;
    }
}
