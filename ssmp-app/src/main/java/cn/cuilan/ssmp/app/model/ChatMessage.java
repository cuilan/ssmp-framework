package cn.cuilan.ssmp.app.model;

import lombok.Data;

/**
 * 聊天消息类型
 *
 * @author zhang.yan
 * @date 2020/1/6
 */
@Data
public class ChatMessage {

    // 消息类型
    private MessageType type;

    // 消息内容
    private String content;

    // 发送人
    private String sender;

    /**
     * 消息类型
     */
    public enum MessageType {
        // 聊天
        CHAT,
        // 加入
        JOIN,
        // 离开
        LEAVE
    }

}
