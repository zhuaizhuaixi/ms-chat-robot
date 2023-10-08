package com.zzx.robot.websocket.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author zzx
 * @date 2023/3/24
 */
public class MessageConstructor {

    public static ObjectNode newGroupMessage(String groupId, String message) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("action", "send_group_msg");
        ObjectNode params = mapper.createObjectNode();
        params.put("group_id", groupId);
        params.put("message", message);
        result.set("params", params);
        return result;
    }

    public static ObjectNode newGuildMessage(String guildId, String channelId, String message) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("action", "send_guild_channel_msg");
        ObjectNode params = mapper.createObjectNode();
        params.put("guild_id", guildId);
        params.put("channel_id", channelId);
        params.put("message", message);
        result.set("params", params);
        return result;
    }

}
