package cn.cuilan.ssmp.app.listener;

import cn.cuilan.ssmp.app.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author zhang.yan
 * @date 2020/1/6
 */
@Component
@Slf4j
public class WebSocketEventListener {

    @Resource
    private SimpMessageSendingOperations messageSendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Principal user = event.getUser();
        log.info("Received a new web socket connection...");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            return;
        }
        String username = (String) sessionAttributes.get("username");
        if (username != null) {
            log.info("User Disconnected: {}", username);

            // 创建一条用户离开的消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            // 转换并发送
            messageSendingOperations.convertAndSend("/topic/public", chatMessage);
        }
    }
}
