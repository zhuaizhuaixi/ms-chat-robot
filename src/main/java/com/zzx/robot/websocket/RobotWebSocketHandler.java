package com.zzx.robot.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.domain.message.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @date 2023/3/24
 */
@Component
public class RobotWebSocketHandler implements WebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(RobotWebSocketHandler.class);

    /**
     * 用线程池执行发来的请求，防止查询超时导致的阻塞
     */
    ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNameFormat("websocket-request-process-%d").build());

    private final List<MessageHandler> handlers;

    public RobotWebSocketHandler(List<MessageHandler> handlerList) {
        handlers = handlerList;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        String payload = (String) message.getPayload();
        logger.info("收到来自go-cqhttp的消息" + payload);
        ObjectMapper om = new ObjectMapper();
        JsonNode messageInfo = om.readTree(payload);
        String postType = messageInfo.get(MessageCommand.CQ_KEY_POST_TYPE) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_POST_TYPE).asText();
        if (MessageCommand.CQ_KEY_MESSAGE.equals(postType)) {

            String messageString = messageInfo.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            logger.info("消息内容: " + messageString);
            String messageType = messageInfo.get(MessageCommand.CQ_KEY_MESSAGE_TYPE).asText();
            String command = messageString.split(" ")[0];

            // 根据commandMessage中的type和command查找出对应的命令处理
            for (MessageHandler handler : handlers) {
                if (handler.match(messageType, command)) {
                    executor.execute(() -> {
                        // 跳过部分群
                        if ("group".equals(messageInfo.get("message_type").asText())) {
                            Set<String> excludeGroup = Sets.newHashSet("293385059", "154681251", "578160115", "232112159");
                            if (excludeGroup.contains(messageInfo.get("group_id").asText())) {
                                return;
                            }
                        }

                        handler.handle(session, messageInfo);
                    });
                    break;
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // 尝试重连
        logger.warn("链接断开：" + closeStatus.toString());


    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
