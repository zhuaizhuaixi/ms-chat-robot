package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.util.MessageConstants;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author zzx
 * @date 2023/6/6
 */
@Component
public class GroupChannelHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupChannelHandler.class);

    Random random = new Random(System.currentTimeMillis());

    @Override
    public ObjectNode handle(Message message) {
        String messageString = message.getBody(String.class);
        logger.info(messageString);
        int channel = random.nextInt(30) + 1;
        return MessageConstructor.newGroupMessage(message.getHeader(MessageConstants.Header.GROUP_ID, String.class), "ch" + channel);
    }
}
