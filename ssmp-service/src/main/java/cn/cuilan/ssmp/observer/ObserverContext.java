package cn.cuilan.ssmp.observer;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 观察者对象上下文
 *
 * @param <T> 实体泛型
 */
public class ObserverContext<T> {

    private Map<String, Object> dataMap;

    @Getter
    @Setter
    T param;

    public ObserverContext() {
        this.dataMap = new HashMap<>();
    }

    public ObserverContext(Map<String, Object> paramMap) {
        this.dataMap = paramMap;
    }

    /**
     * 添加数据
     *
     * @param key   key
     * @param value object
     * @return 观察者对象上下文
     */
    public ObserverContext setData(String key, Object value) {
        dataMap.put(key, value);
        return this;
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return object
     */
    public Object getData(String key) {
        return dataMap.get(key);
    }
}
