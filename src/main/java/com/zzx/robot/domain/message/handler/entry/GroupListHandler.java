package com.zzx.robot.domain.message.handler.entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.message.MessageSender;
import com.zzx.robot.domain.model.entity.Entry;
import com.zzx.robot.domain.repository.EntryRepository;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

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

    @Autowired
    private MessageSender messageSender;

    @Override
    public boolean match(String type, String message) {
        return "group".equals(type) && "词条列表".equals(message);
    }

    @Override
    public void handle(WebSocketSession session, JsonNode message) {

        try {
            String messageString = message.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            logger.info(messageString);

            List<Entry> entries = entryRepository.findAll();

            String reply = entries.stream().map(Entry::getName).collect(Collectors.joining("\n"));

            ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), reply);
            messageSender.send(session, new TextMessage(groupMessage.toString()));

        } catch (Exception e) {
            logger.error("添加词条失败：", e);
        }

    }
}
