package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.message.MessageSender;
import com.zzx.robot.domain.model.entity.User;
import com.zzx.robot.domain.repository.UserRepository;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class GroupBindHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupBindHandler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSender messageSender;

    @Override
    public boolean match(String type, String message) {
        return "group".equals(type) && message.startsWith("绑定");
    }

    @Override
    public void handle(WebSocketSession session, JsonNode message) {

        try {
            String messageString = message.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            logger.info(messageString);
            String characterName = messageString.replace("绑定", "").trim();
            String qq = message.get("user_id").asText();

            User user = userRepository.findByQq(qq);
            if (user != null) {
                user.setMapleName(characterName);
            } else {
                user = new User();
                user.setQq(qq);
                user.setMapleName(characterName);
            }

            userRepository.save(user);
            ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), "绑定成功");
            messageSender.send(session, new TextMessage(groupMessage.toString()));

        } catch (Exception e) {
            logger.error("绑定角色失败：", e);
        }

    }
}
