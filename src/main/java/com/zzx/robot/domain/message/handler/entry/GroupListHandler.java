package com.zzx.robot.domain.message.handler.entry;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.model.entity.Entry;
import com.zzx.robot.domain.repository.EntryRepository;
import com.zzx.robot.util.MessageConstants;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class GroupListHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupListHandler.class);

    @Autowired
    private EntryRepository entryRepository;

    @Override
    public ObjectNode handle(Message message) {

        String messageString = message.getBody(String.class);
        logger.info(messageString);

        List<Entry> entries = entryRepository.findAll();

        String reply = entries.stream().map(Entry::getName).collect(Collectors.joining("\n"));

        return MessageConstructor.newGroupMessage(message.getHeader(MessageConstants.Header.GROUP_ID, String.class), reply);

    }
}
