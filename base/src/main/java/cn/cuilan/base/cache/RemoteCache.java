package cn.cuilan.base.cache;

import cn.cuilan.base.cache.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.*;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class RemoteCache {

    JedisPool jedisPool;

    public RemoteCache(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    <T> T inJedisWithResult(Function<Jedis, T> fun) {
        try (Jedis jedis = jedisPool.getResource()) {
            return fun.apply(jedis);
        }
    }

    private void inJedis(Consumer<Jedis> fun) {
        try (Jedis jedis = jedisPool.getResource()) {
            fun.accept(jedis);
        }
    }

    public <T> Map<T, Double> zrangeByScoreWithScore(String key, String min, String max, int offset, int pageSize) {
        return inJedisWithResult(
                jedis -> jedis.zrevrangeByScoreWithScores(key, min, max, offset, pageSize).stream()
                        .collect(Collectors.toMap(n -> deserialize(n.getBinaryElement()), Tuple::getScore)));
    }

    public <T> Map<T, Double> zrevrangeByScoreWithScore(String key, String max, String min, int offset, int pageSize) {
        return inJedisWithResult(
                jedis -> jedis.zrevrangeByScoreWithScores(key, max, min, offset, pageSize).stream()
                        .collect(Collectors.toMap(n -> deserialize(n.getBinaryElement()), Tuple::getScore)));
    }

    public <T> Map<T, Double> zrangeWithScore(String key, long start, long stop) {
        return inJedisWithResult(
                jedis -> jedis.zrangeWithScores(key, start, stop).stream()
                        .collect(Collectors.toMap(n -> deserialize(n.getBinaryElement()), Tuple::getScore)));
    }

    public <T> List<T> zrangeByScore(String key, String min, String max, int offset, int pageSize) {
        return inJedisWithResult(jedis ->
                jedis.zrangeByScore(toBytes(key), toBytes(min), toBytes(max), offset, pageSize)
                        .stream()
                        .map(n -> (T) deserialize(n))
                        .collect(Collectors.toList()));
    }

    public <T> List<T> zrevrangeByScore(String key, String max, String min, int offset, int pageSize) {
        return inJedisWithResult(jedis ->
                jedis.zrevrangeByScore(toBytes(key), toBytes(max), toBytes(min), offset, pageSize)
                        .stream()
                        .map(n -> (T) deserialize(n))
                        .collect(Collectors.toList()));
    }

    public boolean exist(String key) {
        return inJedisWithResult((jedis) -> jedis.exists(key));
    }


    public <T> boolean setNX(String key, T value, Integer expire, TimeUnit expireUnit) {
        return inJedisWithResult((jedis) -> {
            long result = jedis.setnx(toBytes(key), serialize(value));
            if (expire != null && result == 1) {
                jedis.expire(key, (int) TimeUnit.SECONDS.convert(expire, expireUnit));
                return true;
            }
            return false;
        });
    }

    public boolean tryLock(String key, Integer expire, TimeUnit timeUnit) {
        return setNX(key, "1", expire, timeUnit);

    }

    public <T> void zAdd(String key, T value, double score) {
        inJedis(jedis -> jedis.zadd(toBytes(key), score, serialize(value)));
    }

    public <T> void zAdd(String key, Map<T, Double> datas) {
        inJedis((jedis) -> jedis.zadd(toBytes(key),
                datas.entrySet().stream().collect(
                        Collectors.toMap(
                                n -> serialize(n.getKey()),
                                Map.Entry::getValue
                        ))));
    }

    public boolean expire(String key, Integer expire, TimeUnit expireUnit) {
        if (expire != null) {
            return inJedisWithResult(jedis -> jedis.expire(key, (int) TimeUnit.SECONDS.convert(expire, expireUnit)) == 1);
        }
        return false;
    }

    private <T> DeserializeResult<T> deserialize(Map<String, byte[]> keyBytesMap) {

        DeserializeResult<T> result = new DeserializeResult<>();

        for (Map.Entry<String, byte[]> entry : keyBytesMap.entrySet()) {
            try {
                result.dataMap.put(entry.getKey(), SerializeUtils.deserialize(entry.getValue()));
            } catch (Exception e) {
                result.failedKeys.add(entry.getKey());
            }
        }
        return result;
    }

    private <T> T deserialize(byte[] data) {
        return SerializeUtils.deserialize(data);
    }

    private <T> byte[] serialize(T t) {
        return SerializeUtils.serialize(t);
    }

    public <T> boolean set(String key, T value, Integer expire, TimeUnit expireUnit) {
        return inJedisWithResult(jedis -> {
                    String result = jedis.set(toBytes(key), serialize(value));
                    if (expire != null) {
                        jedis.expire(key, (int) TimeUnit.SECONDS.convert(expire, expireUnit));
                    }
                    return result.equals("OK");
                }
        );
    }


    public <V> Map<String, Long> zrank(Collection<String> keys, V id) {
        return inJedisWithResult(jedis -> {
            Map<String, Long> result = new HashMap<>();
            Pipeline pipeline = jedis.pipelined();
            List<String> keyList;
            if (keys instanceof List) {
                keyList = (List<String>) keys;
            } else {
                keyList = new ArrayList<>(keys);
            }
            for (String key : keyList) {
                pipeline.zrank(toBytes(key), serialize(id));
            }
            List<Object> returns = pipeline.syncAndReturnAll();
            for (int i = 0; i < keyList.size(); i++) {
                result.put(keyList.get(i), (Long) returns.get(i));
            }
            return result;
        });
    }

    public <V> Long zrank(String key, V id) {
        return inJedisWithResult(jedis -> jedis.zrank(toBytes(key), serialize(id)));
    }

    public <T> List<T> zrange(String key, long start, long stop) {
        return inJedisWithResult(jedis -> jedis.zrange(toBytes(key), start, stop))
                .stream()
                .map(n -> (T) deserialize(n))
                .collect(Collectors.toList());
    }

    public <T> List<T> zrevrange(String key, long start, long stop) {
        return inJedisWithResult(jedis -> jedis.zrevrange(toBytes(key), start, stop))
                .stream()
                .map(n -> (T) deserialize(n))
                .collect(Collectors.toList());
    }

    public boolean del(Collection<String> keys) {
        return inJedisWithResult(jedis -> jedis.del(keys.toArray(new String[0])) > 0);
    }

    public boolean del(String key) {
        return inJedisWithResult(jedis -> jedis.del(key) > 0);
    }

    public <V> V get(String key) {
        return inJedisWithResult(jedis -> deserialize(jedis.get(toBytes(key))));
    }

    public <V> void zrem(String key, V id) {
        inJedis(jedis -> jedis.zrem(toBytes(key), serialize(id)));
    }

    public Long zCard(String key) {
        return inJedisWithResult(jedis -> jedis.zcard(key));
    }

    public Long zremrangeByRank(String key, long start, long end) {
        return inJedisWithResult(jedis -> jedis.zremrangeByRank(key, start, end));
    }

    public <T> void hsetAll(String key, Map<String, T> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        inJedis(jedis -> jedis.hmset(toBytes(key), map.entrySet().stream().collect(
                Collectors.toMap(
                        n -> toBytes(n.getKey()),
                        n -> serialize(n.getValue())
                ))));
    }

    public void hsetAllStr(String key, Map<String, String> map) {
        inJedis(jedis -> jedis.hmset(key, map));
    }

    public <T> void hdel(String key, Set<T> set) {
        inJedis(jedis -> jedis.hdel(toBytes(key),
                set.stream().map(this::serialize)
                        .toArray(byte[][]::new)));
    }

    public Long hlen(String key) {
        return inJedisWithResult(jedis -> jedis.hlen(key));
    }

    public void hdel(String key, String field) {
        inJedis(jedis -> jedis.hdel(key, field));
    }

    public <T> void hset(String key, String field, String value) {
        inJedis(jedis -> jedis.hset(toBytes(key), toBytes(field), toBytes(value)));
    }

    public <T> void hset(String key, String field, T value) {
        inJedis(jedis -> jedis.hset(toBytes(key), toBytes(field), serialize(value)));
    }

    public <T> Map<String, T> hmget(String key, Collection<String> fields) {
        return inJedisWithResult(jedis -> {
            List<byte[]> byteList = jedis.hmget(toBytes(key), fields.stream().map(this::toBytes).toArray(byte[][]::new));
            DeserializeResult<T> deserializeResult = deserialize(getKeyBytesMap(fields, byteList));
            if (!CollectionUtils.isEmpty(deserializeResult.failedKeys)) {
                jedis.hdel(toBytes(key), deserializeResult.failedKeys.stream().map(this::toBytes).toArray(byte[][]::new));
            }
            return deserializeResult.dataMap;
        });
    }

    private String toString(byte[] bytes) {
        try {
            return new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    private byte[] toBytes(String str) {
        try {
            return str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return (str).getBytes();
        }
    }

    public <T> T hget(String key, String field) {
        return inJedisWithResult(jedis -> deserialize(jedis.hget(toBytes(key), toBytes(field))));
    }

    public Map<String, String> hget(Collection<String> keyList, String field) {
        return inJedisWithResult(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            Map<String, Response> result = new HashMap<>();
            for (String key : keyList) {
                result.put(key, pipeline.hget(key, field));
            }
            pipeline.sync();
            return result.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey, entry -> String.valueOf(entry.getValue().get())));
        });
    }

    public String hgetStr(String key, String field) {
        return inJedisWithResult(jedis -> jedis.hget(key, field));
    }

    public void hincrBy(String key, String field, long by) {
        inJedis(jedis -> jedis.hincrBy(key, field, by));
    }

    public void hincrByDouble(String key, String field, double by) {
        inJedis(jedis -> jedis.hincrByFloat(key, field, by));
    }

    public Map<String, String> hgetAll(String rawKey) {
        return inJedisWithResult(jedis ->
                jedis.hgetAll(toBytes(rawKey))
                        .entrySet().stream().collect(
                        Collectors.toMap(
                                n -> toString(n.getKey()), n -> toString(n.getValue())
                        )));
    }

    public Map<String, Map<String, String>> hmgetStr(Collection<String> keyList, Collection<String> fieldList) {
        return inJedisWithResult(jedis -> {
            Map<String, Map<String, String>> result = new HashMap<>();
            Pipeline pipeline = jedis.pipelined();
            Map<String, Map<String, Response<String>>> responseMap = new HashMap<>();
            for (String key : keyList) {
                Map<String, Response<String>> fieldResponseMap = new HashMap<>();
                for (String field : fieldList) {
                    fieldResponseMap.put(field, pipeline.hget(key, field));
                }
                responseMap.put(key, fieldResponseMap);
            }
            pipeline.sync();
            responseMap.forEach((key, fieldRespMap) -> {
                Map<String, String> fieldValueMap = new HashMap<>();
                fieldRespMap.forEach((field, resp) -> {
                    String value = resp.get();
                    if (StringUtils.isBlank(value)) {
                        return;
                    }
                    fieldValueMap.put(field, value);
                });
                result.put(key, fieldValueMap);
            });
            return result;
        });
    }

    public Map<String, Boolean> exist(List<String> keyList) {
        return inJedisWithResult(jedis -> {
            Map<String, Boolean> result = new HashMap<>();
            Pipeline pipeline = jedis.pipelined();
            Map<String, Response<Boolean>> keyExistMap = new HashMap<>();
            for (String key : keyList) {
                keyExistMap.put(key, pipeline.exists(key));
            }
            pipeline.sync();
            for (String key : keyList) {
                result.put(key, keyExistMap.get(key).get());
            }
            return result;
        });
    }

    public Long zcount(String key, String min, String max) {
        return inJedisWithResult(jedis -> jedis.zcount(key, min, max));
    }

    private Map<String, byte[]> getKeyBytesMap(Collection<String> keySet, List<byte[]> bytesList) {

        List<String> keyList = new ArrayList<>(keySet);
        Map<String, byte[]> rawKeyBytesResult = new HashMap<>();
        for (int i = 0; i < keyList.size(); i++) {
            byte[] bytes = bytesList.get(i);
            if (bytes != null) {
                rawKeyBytesResult.put(keyList.get(i), bytes);
            }
        }
        return rawKeyBytesResult;
    }

    public <T> Map<String, T> mget(Collection<String> keys) {
        return inJedisWithResult(jedis -> {
            List<String> keyList = new ArrayList<>(keys);
            List<byte[]> bytesList = jedis.mget(
                    keyList.stream().map(this::toBytes).toArray(byte[][]::new)
            );
            DeserializeResult<T> deserializeResult = deserialize(getKeyBytesMap(keys, bytesList));
            if (!CollectionUtils.isEmpty(deserializeResult.failedKeys)) {
                jedis.del(deserializeResult.failedKeys.toArray(new String[0]));
            }
            return deserializeResult.dataMap;
        });
    }

    public void set(String key, Object value, int expireTime) {
        Map map = new HashMap();
        map.put(key, value);
        mset(map, expireTime);
    }

    public void mset(Map<String, ?> dataMap, Integer expireTime) {
        inJedis(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
                pipeline.set(toBytes(entry.getKey()), serialize(entry.getValue()));
                if (expireTime != null) {
                    pipeline.expire(entry.getKey(), expireTime);
                }
            }
            pipeline.sync();
        });
    }

    public void incrBy(String key, Integer num) {
        inJedis(jedis -> jedis.incrBy(key, num));
    }

    public long llen(String key) {
        return inJedisWithResult(jedis -> jedis.llen(key));
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> lrange(String key, int start, int end) {
        return inJedisWithResult(jedis ->
                jedis.lrange(toBytes(key), start, end).stream().map(bytes -> (T) deserialize(bytes)).collect(Collectors.toList())
        );
    }

    public <T> void rpush(String key, T t) {
        inJedis(jedis -> {
            jedis.rpush(toBytes(key), serialize(t));
        });
    }

    public <T> void lpush(String key, T t) {
        inJedis(jedis -> {
            jedis.lpush(toBytes(key), serialize(t));
        });
    }

    public <T> void lpush(String key, List<T> list) {
        List<T> newList = new ArrayList<>(list);
        Collections.reverse(newList);
        inJedis(jedis -> {
            byte[][] dataList = newList.stream().map(this::serialize).toArray(byte[][]::new);
            jedis.lpush(toBytes(key), dataList);
        });
    }

    public <T> T lpop(String key) {
        return inJedisWithResult(jedis -> {
            byte[] bytes = jedis.lpop(toBytes(key));
            if (bytes == null) {
                return null;
            }
            return deserialize(bytes);
        });
    }

    public <T> T rpop(String key) {
        return inJedisWithResult(jedis -> {
            byte[] bytes = jedis.rpop(toBytes(key));
            if (bytes == null) {
                return null;
            }
            return deserialize(bytes);
        });
    }

    public <T> long lrem(String key, long count, T t) {
        return inJedisWithResult(jedis -> jedis.lrem(toBytes(key), count, serialize(t)));
    }

    public <T> List<T> lpop(String key, int size) {
        return inJedisWithResult(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            List<Response<byte[]>> responseList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                responseList.add(pipeline.lpop(toBytes(key)));
            }
            pipeline.sync();
            return responseList.stream().map(response -> {
                byte[] bytes = response.get();
                T t = null;
                if (bytes != null) {
                    t = deserialize(bytes);
                }
                return t;
            }).collect(Collectors.toList());
        });
    }

    public <T> Map<T, Double> zScore(String key, List<T> list) {
        return inJedisWithResult(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            Map<T, Response<Double>> responseMap = new HashMap<>();
            for (T t : list) {
                responseMap.put(t, pipeline.zscore(toBytes(key), serialize(t)));
            }
            pipeline.sync();
//            responseMap.forEach((key1, value) -> log.info("zScore    ===== getKey{} getValue get{}", key1, value.get()));
            return responseMap.entrySet().stream().filter(n -> n.getValue().get() != null)
                    .collect(Collectors.toMap(Map.Entry::getKey, n -> n.getValue().get()));
        });
    }


    class DeserializeResult<T> {
        Map<String, T> dataMap = new HashMap<>();
        Set<String> failedKeys = new HashSet<>();
    }
}
