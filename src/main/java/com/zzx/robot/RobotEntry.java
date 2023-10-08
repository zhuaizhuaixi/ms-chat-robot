package com.zzx.robot;

import com.zzx.robot.websocket.RobotWebSocketHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

/**
 * 请求入口，与cqhttp建立websocket连接，进行交互
 * @author zzx
 */
@Component
public class RobotEntry implements InitializingBean {

    private final RobotWebSocketHandler webSocketHandler;

    public RobotEntry(RobotWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void afterPropertiesSet() {
        // 初始化websocket客户端，并链接
        StandardWebSocketClient client = new StandardWebSocketClient();

        client.doHandshake(webSocketHandler, "ws://localhost:8084");
//        client.doHandshake(webSocketHandler, "ws://121.204.137.241 8084");

    }
}
