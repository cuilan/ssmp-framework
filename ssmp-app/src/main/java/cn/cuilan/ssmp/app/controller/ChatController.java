package cn.cuilan.ssmp.app.controller;

import cn.cuilan.ssmp.app.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 聊天控制层
 *
 * @author zhang.yan
 * @date 2020/1/6
 */
@RestController
@Slf4j
public class ChatController {

    private static LinkedHashMap<Integer, ChatMessage> messageMap = new LinkedHashMap<>();

    private static Integer id = 1;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        log.info(chatMessage.toString());
        messageMap.put(id, chatMessage);
        id++;
        return chatMessage;
    }

    @GetMapping("/app/chat/messages")
    public Map<Integer, ChatMessage> getChatMessages() {
        return messageMap;
    }

    @MessageMapping("/chat/addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // 将用户加入websocket session中
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());
        return chatMessage;
    }

}
