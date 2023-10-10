package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.util.MessageConstants;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Random;

/**
 * @author zzx
 * @date 2023/3/24
 */
@Component
public class GroupPictureHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupPictureHandler.class);

    @Value("${resourceDir}")
    private String resourceDir;

    Random random = new Random(System.currentTimeMillis());

    @Override
    public ObjectNode handle(Message message) {
        String messageString = message.getBody(String.class);
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
                return null;
            }
        }
        logger.info("发送图片:" + pictureFile.getAbsolutePath());
        String qqMessage = "[CQ:image,file=" + pictureFile.getName() + ",subType=0,url=file://" + pictureFile.getAbsolutePath() + "]";
        return MessageConstructor.newGroupMessage(message.getHeader(MessageConstants.Header.GROUP_ID, String.class), qqMessage);

    }

}
