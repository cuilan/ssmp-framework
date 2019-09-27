package cn.cuilan.base.cache.event;

public class CacheEvent {

    private double value;
    private String namespace;
    private CacheEventEnum event;

    public CacheEvent(String namespace, CacheEventEnum event, double value) {
        this.namespace = namespace;
        this.event = event;
        this.value = value;
    }

    public String getNamespace() {
        return namespace;
    }

    public CacheEventEnum getEvent() {
        return event;
    }

    public double getValue() {
        return value;
    }
}
