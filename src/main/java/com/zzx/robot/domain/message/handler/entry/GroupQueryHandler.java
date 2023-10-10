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

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component("groupEntryQueryHandler")
public class GroupQueryHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupQueryHandler.class);

    @Autowired
    private EntryRepository entryRepository;

    @Override
    public ObjectNode handle(Message message) {

        String messageString = message.getBody(String.class);
        logger.info(messageString);
        String name = messageString.replaceFirst("查 ", "").trim();

        List<Entry> entries = entryRepository.findByName(name);
        String reply;
        if (entries.size() > 0) {
            reply = entries.get(0).getContent();
        } else {
            reply = "未找到相关词条";
        }

        return MessageConstructor.newGroupMessage(message.getHeader(MessageConstants.Header.GROUP_ID, String.class), reply);

    }
}
