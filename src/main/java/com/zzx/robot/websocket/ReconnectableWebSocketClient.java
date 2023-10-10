package com.zzx.robot.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 */
@Slf4j
@Component
public class ReconnectableWebSocketClient {

    @Autowired
    private RobotWebSocketHandler robotWebSocketHandler;

    private static final String WEBSOCKET_URI = "ws://121.204.137.241:8084";
    /**
     * Reconnect delay in seconds
     */
    private static final long RECONNECT_DELAY = 5;

    private WebSocketSession currentSession;
    private final WebSocketHandler webSocketHandler;
    private final StandardWebSocketClient webSocketClient;
    private final ScheduledExecutorService executorService;

    public ReconnectableWebSocketClient() {
        webSocketHandler = new MyWebSocketHandler();
        webSocketClient = new StandardWebSocketClient();
        executorService = Executors.newSingleThreadScheduledExecutor();
        connect();
    }

    private void connect() {
        try {
//            if (currentSession != null && currentSession.isOpen()) {
//                currentSession.close();
//            }
            currentSession = webSocketClient.doHandshake(webSocketHandler, WEBSOCKET_URI).get();
        } catch (Exception e) {
            log.error("Failed to connect to WebSocket server: ", e);
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        executorService.schedule(this::connect, RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    private class MyWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            log.info("Connected to WebSocket server");
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            robotWebSocketHandler.handleMessage(session, message);
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            log.error("WebSocket error: ", exception);
            scheduleReconnect();
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            log.info("Disconnected from WebSocket server");
            scheduleReconnect();
        }
    }

    public WebSocketSession getCurrentSession() {
        return currentSession;
    }
}