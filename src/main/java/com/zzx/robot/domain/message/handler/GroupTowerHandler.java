package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.message.MessageSender;
import com.zzx.robot.domain.model.entity.User;
import com.zzx.robot.domain.repository.UserRepository;
import com.zzx.robot.util.DateUtils;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.util.*;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class GroupTowerHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupTowerHandler.class);

    private final Random random = new Random(System.currentTimeMillis());

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private UserRepository userRepository;

    @Value("${maple-info.tower-dir}")
    private String towerImageDir;

    @Override
    public boolean match(String type, String message) {
        return "group".equals(type) && message.startsWith("爬个塔");
    }

    @Override
    public void handle(WebSocketSession session, JsonNode message) {

        try {

            String messageString = message.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            logger.info(messageString);

            // 判断用户今日是否爬过塔
            String qq = message.get("user_id").asText();
            User user = userRepository.findByQq(qq);
            if (user == null) {
                user = new User();
                user.setQq(qq);
                user.setLastTowerTime(new Date());
            } else {
                if (user.getLastTowerTime() != null && user.getLastTowerTime().after(DateUtils.todayInitialDate())) {
                    // 今天已经爬过
                    ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), "今天爬过塔了，明天再来");
                    messageSender.send(session, new TextMessage(groupMessage.toString()));
                    return;
                } else {
                    user.setLastTowerTime(new Date());
                }
            }
            userRepository.save(user);
            StringBuilder qqMessage = new StringBuilder("今日爬塔结果为：\n");
            for (int i = 0; i < 5; i++) {
                String ring = RING_LIST.get(random.nextInt(RING_LIST.size()));
                String level = LEVEL_LIST.get(random.nextInt(LEVEL_LIST.size()));
                String ringPicImage = towerImageDir + File.separator + ring.replace(" ", "_") + ".png";
                if (ring.contains("Ring")) {
                    ring = ring + " " + level;
                }
                qqMessage.append("[CQ:image,file=ring.png,subType=0,url=file://").append(ringPicImage).append("]").append(ring).append("\n");
            }

            ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), qqMessage.toString());
            messageSender.send(session, new TextMessage(groupMessage.toString()));
        } catch (Exception e) {
            logger.error("爬塔失败：", e);
        }


    }

    private static final List<String> RING_LIST = new ArrayList<>();
    private static final List<String> LEVEL_LIST = new ArrayList<>();

    private static final Map<String, Integer> RING_RATE_MAP = new HashMap<String, Integer>() {{
        put("2x EXP Coupon", 909);
        put("Oz Point Pouch", 738);
        put("Broken Box Piece", 3066);
        put("Ocean Glow Earrings", 62);
        put("Berserker Ring", 204);
        put("Clean Defense Ring", 204);
        put("Cleansing Ring", 233);
        put("Clean Stance Ring", 204);
        put("Crisis H Ring", 204);
        put("Critical Damage Ring", 233);
        put("Critical Defense Ring", 233);
        put("Critical Shift Ring", 204);
        put("Durability Ring", 204);
        put("Health Cut Ring", 233);
        put("Level Jump Ring", 233);
        put("Overdrive Ring", 233);
        put("Reflective Ring", 204);
        put("Ring of Restraint", 534);
        put("Risk Taker Ring", 329);
        put("Stance Ring", 204);
        put("Swift Ring", 204);
        put("Totalling Ring", 233);
        put("Tower Boost Ring", 233);
        put("Ultimatum Ring", 329);
        put("Weapon Jump Ring", 329);
    }};

    private static final Map<String, Integer> LEVEL_RATE_MAP = new HashMap<String, Integer>() {{
        put("Level 1", 41);
        put("Level 2", 28);
        put("Level 3", 20);
        put("Level 4", 11);
    }};

    static {
        for (Map.Entry<String, Integer> entry : RING_RATE_MAP.entrySet()) {
            for (Integer i = 0; i < entry.getValue(); i++) {
                RING_LIST.add(entry.getKey());
            }
        }

        for (Map.Entry<String, Integer> entry : LEVEL_RATE_MAP.entrySet()) {
            for (Integer i = 0; i < entry.getValue(); i++) {
                LEVEL_LIST.add(entry.getKey());
            }
        }
    }

}
