package com.zzx.robot.domain.message.handler.entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.message.MessageSender;
import com.zzx.robot.domain.model.entity.Entry;
import com.zzx.robot.domain.model.entity.User;
import com.zzx.robot.domain.repository.EntryRepository;
import com.zzx.robot.domain.repository.UserRepository;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class GroupAddHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupAddHandler.class);

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private MessageSender messageSender;

    @Override
    public boolean match(String type, String message) {
        return "group".equals(type) && message.startsWith("添加词条");
    }

    @Override
    @Transactional
    public void handle(WebSocketSession session, JsonNode message) {

        try {
            String messageString = message.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            logger.info(messageString);
            int firstEnterIndex = messageString.indexOf('\n');
            String firstLine = messageString.substring(0, firstEnterIndex);
            String name = firstLine.replaceFirst("添加词条", "").trim();

            if (!StringUtils.hasText(name)) {
                ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), "没写词条名");
                messageSender.send(session, new TextMessage(groupMessage.toString()));
                return;
            }

            String content = messageString.substring(firstEnterIndex + 1);

            Entry entry = new Entry();
            entry.setName(name);
            entry.setContent(content);

            entryRepository.deleteByName(name);
            entryRepository.save(entry);

            ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), "添加成功");
            messageSender.send(session, new TextMessage(groupMessage.toString()));

        } catch (Exception e) {
            logger.error("添加词条失败：", e);
        }

    }
}
