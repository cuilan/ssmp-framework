package cn.cuilan.ssmp.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPool jedisPool(JedisConnectionFactory jedisConnectionFactory) {
        return new JedisPool(jedisConnectionFactory.getPoolConfig(),
                jedisConnectionFactory.getHostName(),
                jedisConnectionFactory.getPort());
    }
}
