package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.message.MessageSender;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.util.Random;

/**
 * @author zzx
 * @date 2023/3/24
 */
@Component
public class GroupPictureHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupPictureHandler.class);

    @Autowired
    private MessageSender messageSender;

    @Value("${resourceDir}")
    private String resourceDir;

    Random random = new Random(System.currentTimeMillis());

    @Override
    public boolean match(String type, String message) {
        return "group".equals(type) && message.startsWith("来张");
    }

    @Override
    public void handle(WebSocketSession session, JsonNode message) {
        try {
            String messageString = message.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            String target = messageString.replace("来张", "").trim();
            File currentDir = new File(resourceDir + File.separator + target);
            File pictureFile;
            // 从目录中逐层查找非dir的文件
            while (true) {
                if (currentDir.isDirectory()) {
                    File[] files = currentDir.listFiles();
                    currentDir = files[random.nextInt(files.length)];
                } else if (currentDir.isFile()) {
                    pictureFile = currentDir;
                    break;
                } else {
                    return;
                }
            }
            logger.info("发送图片:" + pictureFile.getAbsolutePath());
            String qqMessage = "[CQ:image,file=" + pictureFile.getName() + ",subType=0,url=file://" + pictureFile.getAbsolutePath() + "]";
            ObjectNode objectNode = MessageConstructor.newGroupMessage(message.get("group_id").asText(), qqMessage);
            messageSender.send(session, new TextMessage(objectNode.toString()));
        } catch (Exception e) {
            logger.error("处理消息异常：{}", message.toString(), e);
        }

    }

}
