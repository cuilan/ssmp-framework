package cn.cuilan.base.cache;

import cn.cuilan.framework.utils.NetworkUtils;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存同步器
 *
 * @author zhang.yan
 */
@Slf4j
public class LocalCacheSynchronizer {

    private static String exchangeName = "localCacheSync";
    private static String routingKey = "sync";
    private ConnectionFactory connectionFactory;
    private String queueName = exchangeName + "_" + NetworkUtils.getSelfIp();

    public LocalCacheSynchronizer(String cachePrefix, String mqHost, int mqPort, String username, String password) {
        exchangeName = exchangeName + "_" + cachePrefix;
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(mqHost);
        connectionFactory.setPort(mqPort);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        doConsume();
    }

    private boolean doConsume() {
        return doInRabbitMq(channel -> {
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
            channel.queueDeclare(queueName, false, false, true, null);
            channel.queueBind(queueName, exchangeName, routingKey);
            channel.basicConsume(queueName, true, (consumerTag, message) -> {
                onMessage(message.getBody());
            }, (consumerTag, sig) -> {
                long start = System.currentTimeMillis();
                log.warn("本地缓存同步MQ连接失联,准备重连");
                while (!doConsume()) {
                    try {
                        log.warn("本地缓存同步MQ连接失联,准备重连");
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.warn("本地缓存同步MQ重连成功[耗时={}ms]", System.currentTimeMillis() - start);
            });
        }, false);
    }

    private void doInRabbitMq(MqConsumer consumer) {
        doInRabbitMq(consumer, true);
    }

    private boolean doInRabbitMq(MqConsumer consumer, boolean closeConnection) {
        Connection connection = null;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            consumer.accept(channel);
            return true;
        } catch (Exception e) {
            log.error("", e);
            return false;
        } finally {
            if (closeConnection) {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public <K> void synchronize(String namespace, Collection<K> keys) {
        doInRabbitMq(channel -> {
            Msg msg = new Msg(namespace, keys);
            byte[] data = JSONObject.toJSONString(msg).getBytes(Charsets.UTF_8);
            channel.basicPublish(exchangeName, routingKey, null, data);
        });
    }

    public void onMessage(byte[] message) {
        Msg msg = JSONObject.parseObject(message, Msg.class);
        LocalCache localCache = Caches.localCacheMap.get(msg.nameSpace);
        if (localCache == null) {
            log.error("缓存同步器找不到该缓存[namespace={}]", msg.nameSpace);
            return;
        }
        localCache.delNoSync(msg.keys);
    }

    public void synchronize(String namespace) {
        synchronize(namespace, Collections.EMPTY_LIST);
    }

    interface MqConsumer {
        void accept(Channel channel) throws Exception;
    }

    @Getter
    @Setter
    public static class Msg<K> {
        String nameSpace;
        Collection<K> keys;

        public Msg() {
        }

        public Msg(String nameSpace, Collection<K> keys) {
            this.nameSpace = nameSpace;
            this.keys = keys;
        }
    }
}
