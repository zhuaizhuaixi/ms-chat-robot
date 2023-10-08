package com.zzx.robot.domain.message;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author zzx
 * @date 2023/3/24
 */
public interface MessageHandler {

    /**
     * 返回处理器对应命令的类型
     *
     * @return 命令类型
     */
    boolean match(String type, String message);

    /**
     * 处理消息
     *
     * @param session 用于回传的websocket会话
     * @param message 消息内容
     */
    void handle(WebSocketSession session, JsonNode message);

}
