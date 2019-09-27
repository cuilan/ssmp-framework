package cn.cuilan.base.entity;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class CacheValueSerializer implements RedisSerializer {

    FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o == null) {
            return new byte[0];
        }
        return conf.asByteArray(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return conf.asObject(bytes);
    }
}
