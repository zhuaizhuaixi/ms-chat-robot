package com.zzx.robot.domain.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class MessageSender {

    @Value("${debug-mode:false}")
    private String debugMode;

    public void send(WebSocketSession session, WebSocketMessage<String> message) throws Exception {
        if ("true".equals(debugMode)) {
            System.out.println(message.getPayload());
        } else {
            session.sendMessage(message);
        }

    }

}
