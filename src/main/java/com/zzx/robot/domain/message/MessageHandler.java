package com.zzx.robot.domain.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.camel.Message;

/**
 * @author zzx
 * @date 2023/3/24
 */
public interface MessageHandler {

    /**
     * 处理消息
     *
     * @param message 消息内容
     * @return
     */
    ObjectNode handle(Message message);

}
