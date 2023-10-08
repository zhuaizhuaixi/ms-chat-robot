package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.message.MessageSender;
import com.zzx.robot.domain.model.entity.User;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Random;

/**
 * @author zzx
 * @date 2023/6/6
 */
@Component
public class GroupChannelHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupChannelHandler.class);

    @Autowired
    private MessageSender messageSender;

    @Override
    public boolean match(String type, String message) {
        return "group".equals(type) && message.startsWith("找个线");
    }

    Random random = new Random(System.currentTimeMillis());

    @Override
    public void handle(WebSocketSession session, JsonNode message) {
        try {
            String messageString = message.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            logger.info(messageString);

            int channel = random.nextInt(30) + 1;

            ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), "ch" + channel);
            messageSender.send(session, new TextMessage(groupMessage.toString()));

        } catch (Exception e) {
            logger.error("绑定角色失败：", e);
        }
    }
}
