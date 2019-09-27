package cn.cuilan.base.cache.event;

import cn.cuilan.base.cache.AbstractCache;
import cn.cuilan.base.cache.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class CacheEventCollector {

    private static final int sendPeriodSecond = 1;
    private ScheduledExecutorService executorService;
    private Set<String> namespaces;
    private LinkedBlockingDeque<CacheEvent> eventQueue = new LinkedBlockingDeque<>();
    private DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("MM-dd");

    public CacheEventCollector(Set<String> namespaces, Long showPeriodSecond) {
        this.namespaces = namespaces;
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(this::send, sendPeriodSecond, sendPeriodSecond, TimeUnit.SECONDS);
    }

    private Long getRecordSize(Map<String, String> dataMap) {
        long size = 0;
        if (!CollectionUtils.isEmpty(dataMap)) {
            size += getEventCount(dataMap, CacheEventEnum.REMOTE_REQ, 0);
        }
        return size;
    }

    private long getEventCount(Map<String, String> dataMap, CacheEventEnum eventEnum, long defaultValue) {
        return Long.valueOf(dataMap.getOrDefault(eventEnum.name(), defaultValue + ""));
    }


    public static class CacheStatisticVo {
        String namespace;
        Map<String, String> recordMap;

        public CacheStatisticVo(String namespace) {
            this.namespace = namespace;
        }

        public CacheStatisticVo(String namespace, Map<String, String> recordMap) {
            this.namespace = namespace;
            this.recordMap = recordMap;
        }

        public String getNamespace() {
            return namespace;
        }

        public Map<String, String> getRecordMap() {
            return recordMap;
        }

        public void setRecordMap(Map<String, String> recordMap) {
            this.recordMap = recordMap;
        }
    }

    public List<CacheStatisticVo> show() {
        List<CacheStatisticVo> result = new ArrayList<>();
        for (String namespace : namespaces) {
            CacheStatisticVo vo = new CacheStatisticVo(namespace);
            String recordKey = getRecordKey(namespace);
            Map<String, String> recordMap = AbstractCache.remoteCache.hgetAll(recordKey);
            Map<String, String> sortedRecordMap = new LinkedHashMap<>();
            for (CacheEventEnum en : CacheEventEnum.values()) {
                String value = recordMap.computeIfAbsent(en.name(), n -> "0");
                sortedRecordMap.put(en.name(), value);
            }
            vo.setRecordMap(sortedRecordMap);
            result.add(vo);
        }
        result = result.stream().sorted(
                (vo1, vo2) -> getRecordSize(vo2.getRecordMap()).compareTo(getRecordSize(vo1.getRecordMap())))
                .collect(Collectors.toList());
        return result.stream().peek(vo -> vo.setRecordMap(
                vo.getRecordMap().entrySet().stream().collect(
                        Collectors.toMap(n -> CacheEventEnum.valueOf(n.getKey()).getDesc(), Map.Entry::getValue))
        )).collect(Collectors.toList());

    }

    private String getRecordKey(String namespace) {
        return AbstractCache.remoteCachePrefix + ":##event_record:" + namespace + ":" + LocalDateTime.now().format(hourFormatter);
    }

    private void send() {
        int maxCount = 1000;
        int i = 0;
        Map<String, List<CacheEvent>> currentEventQueueMap = new HashMap<>();
        while (i < maxCount) {
            CacheEvent event = eventQueue.poll();
            if (event == null) {
                break;
            }
            List<CacheEvent> list = currentEventQueueMap.computeIfAbsent(event.getNamespace(), n -> new ArrayList<>());
            list.add(event);
        }
        //循环每个缓存
        for (String namespace : currentEventQueueMap.keySet()) {
            Map<CacheEventEnum, Double> cacheEventValueMap = new HashMap<>();
            //累加同一个事件的值
            for (CacheEvent event : currentEventQueueMap.get(namespace)) {
                Double value = cacheEventValueMap.getOrDefault(event.getEvent(), 0.0);
                cacheEventValueMap.put(event.getEvent(), value + event.getValue());
            }
            for (CacheEventEnum eventEnum : cacheEventValueMap.keySet()) {
                String recordKey = getRecordKey(namespace);
                AbstractCache.remoteCache.hincrByDouble(recordKey, eventEnum.name(), cacheEventValueMap.get(eventEnum));
                AbstractCache.remoteCache.expire(recordKey, 3, TimeUnit.DAYS);
            }
        }
    }

    public void collect(String namespace, CacheEventEnum cacheEventEnum, double value) {
        if (value == 0) {
            return;
        }
        eventQueue.offerLast(new CacheEvent(namespace, cacheEventEnum, value));
    }

    public void collect(String namespace, CacheEventEnum cacheEventEnum) {
        collect(namespace, cacheEventEnum, 1);
    }
}
