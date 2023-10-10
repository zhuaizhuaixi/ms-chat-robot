package com.zzx.robot.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.router.CommandRouter;
import com.zzx.robot.util.MessageConstants;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @date 2023/3/24
 */
@Component
public class RobotWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(RobotWebSocketHandler.class);

    /**
     * 用线程池执行发来的请求，防止查询超时导致的阻塞
     */
    ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNameFormat("websocket-request-process-%d").build());

    private final ProducerTemplate producerTemplate;

    public RobotWebSocketHandler(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        String payload = (String) message.getPayload();
        logger.info("收到来自go-cqhttp的消息" + payload);
        ObjectMapper om = new ObjectMapper();
        JsonNode messageInfo = om.readTree(payload);
        executor.execute(() -> producerTemplate.request("direct:" + CommandRouter.ROUTE_PATH_COMMAND, exchange -> {

            // 将部分信息解析为header
            String postType = messageInfo.get(MessageCommand.CQ_KEY_POST_TYPE) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_POST_TYPE).asText();
            String messageString = messageInfo.get(MessageCommand.CQ_KEY_MESSAGE) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            String messageType = messageInfo.get(MessageCommand.CQ_KEY_MESSAGE_TYPE) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_MESSAGE_TYPE).asText();
            String messageId = messageInfo.get(MessageCommand.CQ_KEY_MESSAGE_ID) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_MESSAGE_ID).asText();
            String userId = messageInfo.get(MessageCommand.CQ_KEY_USER_ID) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_USER_ID).asText();
            String groupId = messageInfo.get(MessageCommand.CQ_KEY_GROUP_ID) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_GROUP_ID).asText();
            String guildId = messageInfo.get(MessageCommand.CQ_KEY_GUILD_ID) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_GUILD_ID).asText();
            String channelId = messageInfo.get(MessageCommand.CQ_KEY_CHANNEL_ID) == null ? "" : messageInfo.get(MessageCommand.CQ_KEY_CHANNEL_ID).asText();
            String briefCommand = messageString.split(" ")[0];

            exchange.getMessage().setHeader(MessageConstants.Header.POST_TYPE, postType);
            exchange.getMessage().setHeader(MessageConstants.Header.BRIEF_COMMAND, briefCommand);
            exchange.getMessage().setHeader(MessageConstants.Header.MESSAGE_TYPE, messageType);
            exchange.getMessage().setHeader(MessageConstants.Header.MESSAGE_ID, messageId);
            exchange.getMessage().setHeader(MessageConstants.Header.SENDER_ID, userId);
            exchange.getMessage().setHeader(MessageConstants.Header.GROUP_ID, groupId);
            exchange.getMessage().setHeader(MessageConstants.Header.GUILD_ID, guildId);
            exchange.getMessage().setHeader(MessageConstants.Header.CHANNEL_ID, channelId);

            exchange.getMessage().setBody(messageString, String.class);
        }));

    }

}
