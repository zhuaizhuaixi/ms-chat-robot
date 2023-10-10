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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class GroupAddHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupAddHandler.class);

    @Autowired
    private EntryRepository entryRepository;

    @Override
    @Transactional
    public ObjectNode handle(Message message) {

        String messageString = message.getBody(String.class);
        logger.info(messageString);
        int firstEnterIndex = messageString.indexOf('\n');
        String firstLine = messageString.substring(0, firstEnterIndex);
        String name = firstLine.replaceFirst("添加词条", "").trim();

        if (!StringUtils.hasText(name)) {
            return MessageConstructor.newGroupMessage(message.getHeader(MessageConstants.Header.GROUP_ID, String.class), "没写词条名");
        }

        String content = messageString.substring(firstEnterIndex + 1);

        Entry entry = new Entry();
        entry.setName(name);
        entry.setContent(content);

        entryRepository.deleteByName(name);
        entryRepository.save(entry);

        return MessageConstructor.newGroupMessage(message.getHeader(MessageConstants.Header.GROUP_ID, String.class), "添加成功");

    }
}
