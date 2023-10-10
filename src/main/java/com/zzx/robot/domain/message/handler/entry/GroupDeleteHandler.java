package com.zzx.robot.domain.message.handler.entry;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.repository.EntryRepository;
import com.zzx.robot.util.MessageConstants;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class GroupDeleteHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupDeleteHandler.class);

    @Autowired
    private EntryRepository entryRepository;

    @Override
    @Transactional
    public ObjectNode handle(Message message) {

        String messageString = message.getBody(String.class);
        logger.info(messageString);
        String name = messageString.replaceFirst("删除词条", "").trim();

        entryRepository.deleteByName(name);

        return MessageConstructor.newGroupMessage(message.getHeader(MessageConstants.Header.GROUP_ID, String.class), "删除成功");

    }
}
