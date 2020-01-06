package cn.cuilan.ssmp.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置类
 * <code>@EnableWebSocketMessageBroker</code> 用于启用WebSocket服务器
 *
 * @author zhang.yan
 * @date 2020/1/6
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 将以 /app 开头的请求都路由到此消息处理方法中
        registry.setApplicationDestinationPrefixes("/app");
        // 内存消息实现
        registry.enableSimpleBroker("/topic");

        // TODO 后续可以将消息改为RabbitMQ实现
//        registry.enableStompBrokerRelay("/topic")
//                .setRelayHost("localhost")
//                .setRelayPort(61616)
//                .setClientLogin("guest")
//                .setClientPasscode("guest");
    }
}
