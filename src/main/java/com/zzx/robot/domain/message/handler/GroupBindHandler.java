package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.model.entity.User;
import com.zzx.robot.domain.repository.UserRepository;
import com.zzx.robot.util.MessageConstants;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class GroupBindHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupBindHandler.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public ObjectNode handle(Message message) {
        String messageString = message.getBody(String.class);
        logger.info(messageString);
        String characterName = messageString.replace("绑定", "").trim();
        String qq = message.getHeader(MessageConstants.Header.SENDER_ID, String.class);

        User user = userRepository.findByQq(qq);
        if (user != null) {
            user.setMapleName(characterName);
        } else {
            user = new User();
            user.setQq(qq);
            user.setMapleName(characterName);
        }

        userRepository.save(user);
        return MessageConstructor.newGroupMessage(message.getHeader(MessageConstants.Header.GROUP_ID, String.class), "绑定成功");

    }
}
