package com.zzx.robot.router;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.util.MessageConstants;
import com.zzx.robot.websocket.ReconnectableWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @author zzx
 * @date 2023/10/10
 */
@Slf4j
@Component
public class SenderRouter extends EndpointRouteBuilder {

    public static final String ROUTE_PATH_SENDER = "/sender";

    private final ReconnectableWebSocketClient webSocketClient;

    @Value("${debug-mode:false}")
    private String debugMode;

    public SenderRouter(ReconnectableWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Override
    public void configure() throws Exception {
        from(direct(ROUTE_PATH_SENDER))
                .process(exchange -> {
                    WebSocketSession session = webSocketClient.getCurrentSession();
                    ObjectNode body = exchange.getMessage().getBody(ObjectNode.class);

                    String messageId = exchange.getMessage().getHeader(MessageConstants.Header.MESSAGE_ID, String.class);

                    if (exchange.getMessage().getHeader(MessageConstants.Header.PROCESSED) != null) {

                        // 改为回复消息
                        ObjectNode message = (ObjectNode) body.get("params");
                        message.put(
                                "message",
                                "[CQ:reply,id=" + messageId + "]" +
                                        message.get("message").asText()
                        );

                        try {
                            if ("true".equals(debugMode)) {
                                System.out.println("发送消息:" + body);
                            } else {
                                session.sendMessage(new TextMessage(body.toString()));
                            }
                        } catch (IOException e) {
                            log.error("发送消息失败: ", e);
                        }
                    }

                });
    }
}
